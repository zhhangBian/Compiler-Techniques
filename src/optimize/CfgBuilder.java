package optimize;

import midend.llvm.instr.BranchInstr;
import midend.llvm.instr.Instr;
import midend.llvm.instr.JumpInstr;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;

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
                Instr lastInstr = visitBlock.GetLastInstr();
                // 如果是jump
                if (lastInstr instanceof JumpInstr jumpInstr) {
                    IrBasicBlock targetBlock = jumpInstr.GetTargetBlock();
                    visitBlock.AddNextBlock(targetBlock);
                    targetBlock.AddBeforeBlock(visitBlock);
                }
                // 如果是branch
                else if (lastInstr instanceof BranchInstr branchInstr) {
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

    // 支配关系
    // 结点删除法：总起始结点可达所
    // 如果我们删去图中的某一个结点后，有一些结点变得不可到达，那么这个被删去的结点支配这些变得不可到达的结点
    public void BuildDominateRelationship() {

    }


}
