package optimize;

import midend.llvm.instr.BranchInstr;
import midend.llvm.instr.Instr;
import midend.llvm.instr.JumpInstr;
import midend.llvm.instr.ReturnInstr;
import midend.llvm.use.IrUse;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;

import java.util.ArrayList;
import java.util.Iterator;

public class RemoveDeadBlock extends Optimizer {
    @Override
    public void Optimize() {
        for (IrFunction irFunction : irModule.GetFunctions()) {
            ArrayList<IrBasicBlock> removerBlocks = new ArrayList<>();
            boolean changed = true;
            while (changed) {
                changed = false;
                ArrayList<IrBasicBlock> blockList = irFunction.GetBasicBlocks();
                int count = blockList.size();
                Iterator<IrBasicBlock> iterator = blockList.iterator();
                while (iterator.hasNext() && count > 1) {
                    IrBasicBlock block = iterator.next();
                    if (block.IsEmptyBlock()) {
                        removerBlocks.add(block);
                        iterator.remove();
                        count--;
                        changed = true;
                    } else if (this.IsDeadBlock(block)) {
                        removerBlocks.add(block);
                        this.KillBlock(block);
                        iterator.remove();
                        count--;
                        changed = true;
                    }
                }
            }
            // 去除use
            for (IrBasicBlock block : removerBlocks) {
                // value不再登记user
                ArrayList<IrUse> uses = new ArrayList<>(block.GetUseList());
                for (IrUse irUse : uses) {
                    irUse.GetValue().DeleteUser(irUse.GetUser());
                }
                // 去除使用的value
                block.GetUseValueSet().clear();
            }
        }
    }

    private boolean IsDeadBlock(IrBasicBlock irBasicBlock) {
        Instr lastInstr = irBasicBlock.GetLastInstr();
        boolean isDead = irBasicBlock.GetInstrList().size() == 1 &&
            (lastInstr instanceof JumpInstr || lastInstr instanceof BranchInstr ||
                lastInstr instanceof ReturnInstr);

        for (IrBasicBlock beforeBlock : irBasicBlock.GetBeforeBlocks()) {
            if (!(beforeBlock.GetLastInstr() instanceof JumpInstr)) {
                return false;
            }
        }

        return isDead;
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
            for (IrBasicBlock nextBlock : nextBlocks) {
                nextBlock.ReplaceBeforeBlock(visitBlock);
            }
        }
    }
}
