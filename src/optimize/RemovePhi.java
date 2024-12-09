package optimize;

import backend.mips.Register;
import midend.llvm.constant.IrConstant;
import midend.llvm.instr.Instr;
import midend.llvm.instr.MoveInstr;
import midend.llvm.instr.phi.ParallelCopyInstr;
import midend.llvm.instr.phi.PhiInstr;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;
import midend.llvm.value.IrValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class RemovePhi extends Optimizer {
    @Override
    public void Optimize() {
        this.ConvertPhiToParallelCopy();
        this.ConvertParallelCopyToMove();
    }

    private void ConvertPhiToParallelCopy() {
        for (IrFunction irFunction : irModule.GetFunctions()) {
            ArrayList<IrBasicBlock> blockList = new ArrayList<>(irFunction.GetBasicBlocks());
            for (IrBasicBlock irBasicBlock : blockList) {
                // 确保有phi指令
                if (!(irBasicBlock.GetFirstInstr() instanceof PhiInstr)) {
                    continue;
                }

                ArrayList<ParallelCopyInstr> copyList = new ArrayList<>();
                // 遍历前驱基本快，将copy指令插入适当的位置
                // 1. 如果before只有一个后继，直接插入前驱块
                // 2. 如果before有多个后继，需要新建一个中间块
                for (IrBasicBlock beforeBlock : irBasicBlock.GetBeforeBlocks()) {
                    ParallelCopyInstr copyInstr = beforeBlock.GetNextBlocks().size() == 1 ?
                        this.InsertCopyDirect(beforeBlock) :
                        this.InsertCopyToMiddle(beforeBlock, irBasicBlock);
                    copyList.add(copyInstr);
                }

                // 向phi的copy中填充相应值，可能有多个phi
                Iterator<Instr> iterator = irBasicBlock.GetInstrList().iterator();
                while (iterator.hasNext()) {
                    Instr instr = iterator.next();
                    if (instr instanceof PhiInstr phiInstr) {
                        ArrayList<IrValue> useValueList = phiInstr.GetUseValueList();
                        // 把phi相应的值拷贝到phi指令
                        for (int i = 0; i < useValueList.size(); i++) {
                            IrValue useValue = useValueList.get(i);
                            copyList.get(i).AddCopy(useValue, phiInstr);
                        }
                        iterator.remove();
                    }
                }
            }
        }
    }

    // 直接插入到前驱块
    private ParallelCopyInstr InsertCopyDirect(IrBasicBlock beforeBlock) {
        ParallelCopyInstr copyInstr = new ParallelCopyInstr(beforeBlock);
        beforeBlock.AddInstrBeforeJump(copyInstr);
        return copyInstr;
    }

    // 创建新的中间块并插入
    private ParallelCopyInstr InsertCopyToMiddle(IrBasicBlock beforeBlock, IrBasicBlock nextBlock) {
        IrBasicBlock middleBlock = IrBasicBlock.AddMiddleBlock(beforeBlock, nextBlock);
        ParallelCopyInstr copyInstr = new ParallelCopyInstr(middleBlock);
        middleBlock.AddInstrBeforeJump(copyInstr);
        return copyInstr;
    }

    // 将copy指令转化为一系列move
    private void ConvertParallelCopyToMove() {
        for (IrFunction irFunction : irModule.GetFunctions()) {
            for (IrBasicBlock irBasicBlock : irFunction.GetBasicBlocks()) {
                if (irBasicBlock.HaveParallelCopyInstr()) {
                    // 获取并产出copy
                    ParallelCopyInstr copyInstr = irBasicBlock.GetAndRemoveParallelCopyInstr();
                    // 转化为一系列move
                    this.ConvertCopyToMove(copyInstr, irBasicBlock);
                }
            }
        }
    }

    private void ConvertCopyToMove(ParallelCopyInstr copyInstr, IrBasicBlock irBasicBlock) {
        // 遍历同时检查冲突现象：后面的move的src为前序的dst
        ArrayList<MoveInstr> moveList = this.ConvertCopy(copyInstr, irBasicBlock);
        // 检查循环赋值冲突
        ArrayList<MoveInstr> circleList =
            this.CheckCircleConflict(copyInstr, irBasicBlock, moveList);
        // 检查寄存器冲突
        ArrayList<MoveInstr> registerList = this.CheckRegisterConflict(moveList, irBasicBlock);
        // 在跳转前加入move
        circleList.addAll(registerList);
        moveList.addAll(0, circleList);
        moveList.forEach(irBasicBlock::AddInstrBeforeJump);
    }

    private ArrayList<MoveInstr> ConvertCopy(ParallelCopyInstr copyInstr,
                                             IrBasicBlock irBasicBlock) {
        ArrayList<IrValue> srcList = copyInstr.GetSrcList();
        ArrayList<IrValue> dstList = copyInstr.GetDstList();

        ArrayList<MoveInstr> moveList = new ArrayList<>();
        for (int i = 0; i < dstList.size(); i++) {
            moveList.add(new MoveInstr(srcList.get(i), dstList.get(i), irBasicBlock));
        }

        return moveList;
    }

    private ArrayList<MoveInstr> CheckCircleConflict(
        ParallelCopyInstr copyInstr, IrBasicBlock irBasicBlock, ArrayList<MoveInstr> moveList) {
        ArrayList<IrValue> dstList = copyInstr.GetDstList();

        ArrayList<MoveInstr> fixList = new ArrayList<>();
        HashSet<IrValue> valueRecord = new HashSet<>();
        for (int i = 0; i < moveList.size(); i++) {
            IrValue dstValue = dstList.get(i);

            if (!(dstValue instanceof IrConstant) && !valueRecord.contains(dstValue)) {
                if (this.HaveCircleConflict(copyInstr, i)) {
                    IrValue middleValue = new IrValue(dstValue.GetIrType(),
                        dstValue.GetIrName() + "_tmp");
                    moveList.add(0, new MoveInstr(dstValue, middleValue, irBasicBlock));
                    // 替换后续指令的src
                    for (MoveInstr moveInstr : moveList) {
                        if (moveInstr.GetSrcValue().equals(dstValue)) {
                            moveInstr.SetSrcValue(middleValue);
                        }
                    }
                    fixList.add(new MoveInstr(middleValue, dstValue, irBasicBlock));
                }
                valueRecord.add(dstValue);
            }
        }
        return fixList;
    }

    private boolean HaveCircleConflict(ParallelCopyInstr copyInstr, int index) {
        ArrayList<IrValue> srcList = copyInstr.GetSrcList();
        ArrayList<IrValue> dstList = copyInstr.GetDstList();
        IrValue dstValue = dstList.get(index);
        for (int i = index + 1; i < srcList.size(); i++) {
            if (srcList.get(i).equals(dstValue)) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<MoveInstr> CheckRegisterConflict(ArrayList<MoveInstr> moveList,
                                                       IrBasicBlock irBasicBlock) {
        ArrayList<MoveInstr> fixList = new ArrayList<>();
        HashSet<IrValue> valueRecord = new HashSet<>();
        for (int i = moveList.size() - 1; i >= 0; i--) {
            IrValue srcValue = moveList.get(i).GetSrcValue();
            if (!(srcValue instanceof IrConstant) && !valueRecord.contains(srcValue)) {
                if (this.HaveRegisterConflict(moveList, i, irBasicBlock)) {
                    IrValue middleValue = new IrValue(srcValue.GetIrType(),
                        srcValue.GetIrName() + "_tmp");
                    // 将所有相同指令的src替换为临时
                    for (MoveInstr moveInstr : moveList) {
                        if (moveInstr.GetSrcValue() == srcValue) {
                            moveInstr.SetSrcValue(middleValue);
                        }
                    }
                    // 在moveList开头加入新move
                    MoveInstr moveInstr = new MoveInstr(srcValue, middleValue, irBasicBlock);
                    fixList.add(moveInstr);
                }
                valueRecord.add(srcValue);
            }
        }
        return fixList;
    }

    private boolean HaveRegisterConflict(ArrayList<MoveInstr> moveList, int index,
                                         IrBasicBlock irBasicBlock) {
        HashMap<IrValue, Register> registerMap = irBasicBlock.GetIrFunction().GetValueRegisterMap();
        IrValue srcValue = moveList.get(index).GetSrcValue();
        Register srcRegister = registerMap.get(srcValue);

        if (srcRegister != null) {
            for (int i = 0; i < index; i++) {
                IrValue dstValue = moveList.get(i).GetDstValue();
                if (registerMap.get(dstValue).equals(srcRegister)) {
                    return true;
                }
            }
        }
        return false;
    }
}
