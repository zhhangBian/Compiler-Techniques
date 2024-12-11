package optimize;

import midend.llvm.instr.BranchInstr;
import midend.llvm.instr.Instr;
import midend.llvm.instr.JumpInstr;
import midend.llvm.instr.ReturnInstr;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;

import java.util.ArrayList;
import java.util.Iterator;

// 不考虑phi，只在mem2reg前进行
public class RemoveDeadBlock extends Optimizer {
    @Override
    public void Optimize() {
        // 删除无用Jump
        this.RemoveJump();
        // 合并基本块
        this.MergeBlock();
    }

    private void RemoveJump() {
        for (IrFunction irFunction : irModule.GetFunctions()) {
            boolean changed = true;
            while (changed) {
                changed = false;
                ArrayList<IrBasicBlock> blockList = irFunction.GetBasicBlocks();
                int count = blockList.size();
                Iterator<IrBasicBlock> iterator = blockList.iterator();
                while (iterator.hasNext() && count > 1) {
                    IrBasicBlock block = iterator.next();
                    if (this.IsDeadBlock(block)) {
                        this.KillBlock(block);
                        iterator.remove();
                        count--;
                        changed = true;
                    }
                }
            }
        }
    }

    private boolean IsDeadBlock(IrBasicBlock irBasicBlock) {
        Instr lastInstr = irBasicBlock.GetLastInstr();
        boolean isDead = irBasicBlock.GetInstrList().size() == 1 &&
            (lastInstr instanceof JumpInstr || lastInstr instanceof BranchInstr ||
                lastInstr instanceof ReturnInstr);
        // 之前的block不为branch，才可以进行删除
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

    // 合并基本块：前序只到该基本块，则可以进行合并
    private void MergeBlock() {
        for (IrFunction irFunction : irModule.GetFunctions()) {
            boolean changed = true;
            while (changed) {
                changed = false;
                Iterator<IrBasicBlock> iterator = irFunction.GetBasicBlocks().iterator();
                while (iterator.hasNext()) {
                    IrBasicBlock visitBlock = iterator.next();
                    if (this.CanMergeBlock(visitBlock)) {
                        IrBasicBlock beforeBlock = visitBlock.GetBeforeBlocks().get(0);
                        beforeBlock.AppendBlock(visitBlock);
                        iterator.remove();
                        changed = true;
                    }
                }
            }
        }
    }

    private boolean CanMergeBlock(IrBasicBlock visitBlock) {
        ArrayList<IrBasicBlock> beforeBlockList = visitBlock.GetBeforeBlocks();
        if (beforeBlockList.size() == 1) {
            IrBasicBlock beforeBlock = beforeBlockList.get(0);
            // 前后对接上，则可以合并
            return beforeBlock.GetNextBlocks().size() == 1;
        }
        return false;
    }
}
