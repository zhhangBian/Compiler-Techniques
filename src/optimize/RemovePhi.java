package optimize;

import midend.llvm.instr.Instr;
import midend.llvm.instr.MoveInstr;
import midend.llvm.instr.phi.ParallelCopyInstr;
import midend.llvm.instr.phi.PhiInstr;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;
import midend.llvm.value.IrValue;

import java.util.ArrayList;
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
                ArrayList<IrBasicBlock> beforeBlocks = irBasicBlock.GetBeforeBlock();
                for (IrBasicBlock beforeBlock : beforeBlocks) {
                    ParallelCopyInstr copyInstr = beforeBlock.GetNextBlocks().size() == 1 ?
                        // 只有唯一后继
                        this.InsertCopyDirect(beforeBlock) :
                        // 有多个后继
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
        ArrayList<IrValue> srcList = copyInstr.GetSrcList();
        ArrayList<IrValue> dstList = copyInstr.GetDstList();

        ArrayList<Instr> moveList = new ArrayList<>();
        // 在遍历的同时检查冲突现象：后面的move的src为前序的dst
        for (int i = 0; i < dstList.size(); i++) {
            IrValue srcValue = srcList.get(i);
            IrValue dstValue = dstList.get(i);

            moveList.add(new MoveInstr(srcValue, dstValue, irBasicBlock));
            if (this.CheckConflict(copyInstr, i)) {
                IrValue middleValue = new IrValue(srcValue.GetIrType(),
                    dstValue.GetIrName() + "_tmp");
                moveList.add(new MoveInstr(srcValue, middleValue, irBasicBlock));
                // 替换后续指令的src
                for (int j = i + 1; j < dstList.size(); j++) {
                    srcList.set(j, middleValue);
                }
            }
        }

        // 在块首加入move
        for (int i = moveList.size() - 1; i >= 0; i--) {
            irBasicBlock.AddInstr(moveList.get(i), 0);
        }
    }

    private boolean CheckConflict(ParallelCopyInstr copyInstr, int index) {
        ArrayList<IrValue> srcList = copyInstr.GetSrcList();
        ArrayList<IrValue> dstList = copyInstr.GetDstList();
        IrValue dstValue = dstList.get(index);
        for (int i = index + 1; i < srcList.size(); i++) {
            if (srcList.get(i) == dstValue) {
                return true;
            }
        }
        return false;
    }
}
