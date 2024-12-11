package optimize;

import midend.llvm.instr.BranchInstr;
import midend.llvm.instr.Instr;
import midend.llvm.instr.JumpInstr;
import midend.llvm.instr.ReturnInstr;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;

import java.util.HashSet;
import java.util.Iterator;

public class RemoveUnReachCode extends Optimizer {
    @Override
    public void Optimize() {
        // 删除多余的jump
        this.RemoveUselessJump();
        // 删除不可达块
        this.RemoveUselessBlock();
    }

    private void RemoveUselessJump() {
        for (IrFunction irFunction : irModule.GetFunctions()) {
            for (IrBasicBlock irBasicBlock : irFunction.GetBasicBlocks()) {
                boolean hasJump = false;
                Iterator<Instr> iterator = irBasicBlock.GetInstrList().iterator();
                while (iterator.hasNext()) {
                    Instr instr = iterator.next();
                    if (hasJump) {
                        instr.RemoveAllValueUse();
                        iterator.remove();
                        continue;
                    }

                    if (instr instanceof JumpInstr || instr instanceof BranchInstr ||
                        instr instanceof ReturnInstr) {
                        hasJump = true;
                    }
                }
            }
        }
    }

    private void RemoveUselessBlock() {
        for (IrFunction irFunction : irModule.GetFunctions()) {
            IrBasicBlock entryBlock = irFunction.GetBasicBlocks().get(0);
            HashSet<IrBasicBlock> visited = new HashSet<>();
            // 使用dfs记录可达的block
            this.DfsBlock(entryBlock, visited);
            irFunction.GetBasicBlocks().removeIf(block -> !visited.contains(block));
        }
    }

    private void DfsBlock(IrBasicBlock block, HashSet<IrBasicBlock> visited) {
        if (visited.contains(block)) {
            return;
        }

        visited.add(block);
        // 一定是跳转
        Instr instr = block.GetLastInstr();
        // return
        if (instr instanceof ReturnInstr) {
            return;
        }
        // jump
        else if (instr instanceof JumpInstr jumpInstr) {
            IrBasicBlock targetBlock = jumpInstr.GetTargetBlock();
            this.DfsBlock(targetBlock, visited);
        }
        // branch
        else if (instr instanceof BranchInstr branchInstr) {
            IrBasicBlock trueBlock = branchInstr.GetTrueBlock();
            IrBasicBlock falseBlock = branchInstr.GetFalseBlock();
            this.DfsBlock(trueBlock, visited);
            this.DfsBlock(falseBlock, visited);
        }
    }
}
