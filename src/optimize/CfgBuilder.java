package optimize;

import midend.llvm.instr.BranchInstr;
import midend.llvm.instr.Instr;
import midend.llvm.instr.JumpInstr;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;

import java.util.ArrayList;
import java.util.HashSet;

// 构建CFG支配图
public class CfgBuilder extends Optimizer {
    @Override
    public void Optimize() {
        // 清楚之前生成的支配关系
        this.InitFunction();
        // 构建CFG图
        this.BuildCfg();
        // 构建支配关系
        this.BuildDominateRelationship();
        // 构建直接支配关系
        this.BuildDirectDominator();
        // 构建支配边界
        this.BuildDominateFrontier();
    }

    private void InitFunction() {
        for (IrFunction irFunction : irModule.GetFunctions()) {
            for (IrBasicBlock irBasicBlock : irFunction.GetBasicBlocks()) {
                irBasicBlock.ClearCfg();
            }
        }
    }

    private void BuildCfg() {
        // 构建CFG图
        for (IrFunction irFunction : irModule.GetFunctions()) {
            for (IrBasicBlock visitBlock : irFunction.GetBasicBlocks()) {
                for (Instr instr : visitBlock.GetInstrList()) {
                    // 如果是jump
                    if (instr instanceof JumpInstr jumpInstr) {
                        IrBasicBlock targetBlock = jumpInstr.GetTargetBlock();
                        visitBlock.AddNextBlock(targetBlock);
                        targetBlock.AddBeforeBlock(visitBlock);
                    }
                    // 如果是branch
                    else if (instr instanceof BranchInstr branchInstr) {
                        IrBasicBlock trueBlock = branchInstr.GetTrueBlock();
                        IrBasicBlock falseBlock = branchInstr.GetFalseBlock();
                        visitBlock.AddNextBlock(trueBlock);
                        visitBlock.AddNextBlock(falseBlock);
                        trueBlock.AddBeforeBlock(visitBlock);
                        falseBlock.AddBeforeBlock(visitBlock);
                    }
                }
            }
        }
    }

    // 构建支配关系，使用结点删除法：
    // 如果删去图中的某一个结点后，有一些结点变得不可到达，那么这个被删去的结点支配这些变得不可到达的结点
    private void BuildDominateRelationship() {
        for (IrFunction irFunction : irModule.GetFunctions()) {
            ArrayList<IrBasicBlock> blockList = irFunction.GetBasicBlocks();
            for (IrBasicBlock deleteBlock : blockList) {
                HashSet<IrBasicBlock> visited = new HashSet<>();
                // 总起始结点可达所有结点
                this.SearchDfs(blockList.get(0), deleteBlock, visited);
                for (IrBasicBlock visitBlock : blockList) {
                    // 被删除的结点支配
                    if (!visited.contains(visitBlock)) {
                        visitBlock.AddDominator(deleteBlock);
                    }
                }
            }
        }
    }

    private void SearchDfs(IrBasicBlock visitBlock, IrBasicBlock deleteBlock,
                           HashSet<IrBasicBlock> visited) {
        if (visitBlock == deleteBlock) {
            return;
        }

        visited.add(visitBlock);
        for (IrBasicBlock nextBlock : visitBlock.GetNextBlocks()) {
            if (!visited.contains(nextBlock) && nextBlock != deleteBlock) {
                this.SearchDfs(nextBlock, deleteBlock, visited);
            }
        }
    }

    // 构建结点的直接支配关系
    private void BuildDirectDominator() {
        for (IrFunction irFunction : irModule.GetFunctions()) {
            for (IrBasicBlock visitBlock : irFunction.GetBasicBlocks()) {
                // 通过删去共同的支配者，来找出最短路径
                for (IrBasicBlock dominator : visitBlock.GetDominatorBlocks()) {
                    HashSet<IrBasicBlock> sharedDominators =
                        new HashSet<>(visitBlock.GetDominatorBlocks());
                    // 保留共同支配者
                    sharedDominators.retainAll(dominator.GetDominatorBlocks());

                    HashSet<IrBasicBlock> diffDominators =
                        new HashSet<>(visitBlock.GetDominatorBlocks());
                    // 和支配者不同的支配结点
                    diffDominators.removeAll(sharedDominators);
                    // 支配者的支配着集合中仅有自身，说明直接支配
                    if (diffDominators.size() == 1 && diffDominators.contains(visitBlock)) {
                        visitBlock.AddDirectDominatorRelationship(dominator);
                        break;
                    }
                }
            }
        }
    }

    private void BuildDominateFrontier() {
        for (IrFunction irFunction : irModule.GetFunctions()) {
            for (IrBasicBlock visitBlock : irFunction.GetBasicBlocks()) {
                ArrayList<IrBasicBlock> nextBlocksBlocks = visitBlock.GetNextBlocks();
                for (IrBasicBlock nextBlock : nextBlocksBlocks) {
                    // 指针，沿着直接支配关系进行上溯
                    IrBasicBlock currentBlock = visitBlock;
                    // 后继块就是 cur 或者是 nextBlock 的支配者不包括 cur
                    while (!nextBlock.GetDominatorBlocks().contains(currentBlock) ||
                        currentBlock == nextBlock) {
                        currentBlock.AddDominateFrontier(nextBlock);
                        // 进行上溯
                        currentBlock = currentBlock.GetDirectDominator();
                        if (currentBlock == null) {
                            break;
                        }
                    }
                }
            }
        }
    }
}
