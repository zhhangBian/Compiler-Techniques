package optimize;

import midend.llvm.instr.BranchInstr;
import midend.llvm.instr.Instr;
import midend.llvm.instr.JumpInstr;
import midend.llvm.instr.ReturnInstr;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;

import java.util.ArrayList;
import java.util.Iterator;

public class RemoveDeadBlock extends Optimizer {
    @Override
    public void Optimize() {
        for (IrFunction irFunction : irModule.GetFunctions()) {
            ArrayList<IrBasicBlock> blockList = irFunction.GetBasicBlocks();
            Iterator<IrBasicBlock> iterator = blockList.iterator();
            while (iterator.hasNext()) {
                IrBasicBlock block = iterator.next();
                if (block.IsEmptyBlock()) {
                    this.KillBlock(block);
                    iterator.remove();
                }
            }
        }
    }

    private boolean IsDeadBlock(IrBasicBlock irBasicBlock) {
        Instr lastInstr = irBasicBlock.GetLastInstr();
        return irBasicBlock.GetInstrList().size() == 1 &&
            (lastInstr instanceof JumpInstr || lastInstr instanceof BranchInstr ||
                lastInstr instanceof ReturnInstr);
    }

    private void KillBlock(IrBasicBlock visitBlock) {
        Instr jumpInstr = visitBlock.GetLastInstr();
        ArrayList<IrBasicBlock> beforeBlocks = visitBlock.GetBeforeBlocks();
        ArrayList<IrBasicBlock> nextBlocks = visitBlock.GetNextBlocks();

        for (IrBasicBlock beforeBlock : beforeBlocks) {
            // 清除原先的跳转
            beforeBlock.ReplaceLastInstr(jumpInstr);
            // 更改before-next关系
            beforeBlock.ReplaceNextBlock(visitBlock);
        }

        for (IrBasicBlock nextBlock : nextBlocks) {
            nextBlock.ReplaceBeforeBlock(visitBlock);
        }
    }
}
