package midend.llvm.value;

import backend.mips.assembly.MipsLabel;
import midend.llvm.IrBuilder;
import midend.llvm.instr.BranchInstr;
import midend.llvm.instr.Instr;
import midend.llvm.instr.JumpInstr;
import midend.llvm.instr.ReturnInstr;
import midend.llvm.instr.phi.ParallelCopyInstr;
import midend.llvm.instr.phi.PhiInstr;
import midend.llvm.type.IrBasicBlockType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public class IrBasicBlock extends IrValue {
    private final ArrayList<Instr> instrList;
    private final IrFunction irFunction;
    // 描述前驱后继的数据结构
    private final ArrayList<IrBasicBlock> nextBlockList;
    private final ArrayList<IrBasicBlock> beforeBlockList;

    // 描述支配关系的数据结构
    // 支配该block的block
    private final HashSet<IrBasicBlock> dominators;
    // 该结点的直接支配者
    private IrBasicBlock directDominator;
    // 该结点直接支配的结点
    private final HashSet<IrBasicBlock> directDominateBlocks;
    // 该节点的支配边界
    private final HashSet<IrBasicBlock> dominateFrontiers;

    // 描述活跃变量分析的数据结构
    private HashSet<IrValue> inValueSet;
    private HashSet<IrValue> outValueSet;
    private HashSet<IrValue> defValueSet;
    private HashSet<IrValue> useValueSet;

    public IrBasicBlock(String irName, IrFunction irFunction) {
        super(IrBasicBlockType.BASIC_BLOCK, irName);
        this.instrList = new ArrayList<>();
        this.irFunction = irFunction;
        // 描述前驱后继的数据结构
        this.nextBlockList = new ArrayList<>();
        this.beforeBlockList = new ArrayList<>();
        // 描述支配关系的数据结构
        this.dominators = new HashSet<>();
        this.directDominator = null;
        this.directDominateBlocks = new HashSet<>();
        this.dominateFrontiers = new HashSet<>();
        // 描述活跃变量分析的数据结构
        this.inValueSet = new HashSet<>();
        this.outValueSet = new HashSet<>();
        this.defValueSet = new HashSet<>();
        this.useValueSet = new HashSet<>();
    }

    public boolean IsEmptyBlock() {
        return this.instrList.isEmpty();
    }

    public boolean IsEntryBlock() {
        return this.irFunction.GetBasicBlocks().get(0) == this;
    }

    public void AddInstr(Instr instr) {
        this.instrList.add(instr);
        instr.SetInBasicBlock(this);
    }

    public void AddInstr(Instr instr, int index) {
        this.instrList.add(index, instr);
    }

    public void ReplaceLastInstr(Instr instr) {
        Instr jumpInstr = this.GetLastInstr();
        this.instrList.remove(jumpInstr);
        jumpInstr.RemoveAllValueUse();
        this.AddInstr(instr);
    }

    public void AddInstrBeforeJump(Instr instr) {
        Instr lastInstr = this.GetLastInstr();
        if (lastInstr instanceof JumpInstr || lastInstr instanceof BranchInstr) {
            this.instrList.add(this.instrList.size() - 1, instr);
        } else {
            this.instrList.add(instr);
        }
        instr.SetInBasicBlock(this);
    }

    public ArrayList<Instr> GetInstrList() {
        return this.instrList;
    }

    public Instr GetFirstInstr() {
        return this.instrList.get(0);
    }

    public Instr GetLastInstr() {
        return this.instrList.get(this.instrList.size() - 1);
    }

    public ParallelCopyInstr GetAndRemoveParallelCopyInstr() {
        ParallelCopyInstr copyInstr =
            (ParallelCopyInstr) this.instrList.get(this.instrList.size() - 2);
        this.instrList.remove(this.instrList.size() - 2);
        return copyInstr;
    }

    public IrFunction GetIrFunction() {
        return this.irFunction;
    }

    public boolean LastInstrIsReturn() {
        if (this.instrList.isEmpty()) {
            return false;
        }
        return this.instrList.get(this.instrList.size() - 1) instanceof ReturnInstr;
    }

    public boolean HaveParallelCopyInstr() {
        return this.instrList.size() > 1 &&
            this.instrList.get(this.instrList.size() - 2) instanceof ParallelCopyInstr;
    }

    public void ClearCfg() {
        // 可达关系
        this.nextBlockList.clear();
        this.beforeBlockList.clear();
        // 支配关系
        this.dominators.clear();
        this.directDominator = null;
        this.directDominateBlocks.clear();
        this.dominateFrontiers.clear();
    }

    public ArrayList<IrBasicBlock> GetNextBlocks() {
        return this.nextBlockList;
    }

    public ArrayList<IrBasicBlock> GetBeforeBlocks() {
        return this.beforeBlockList;
    }

    public HashSet<IrBasicBlock> GetDominatorBlocks() {
        return this.dominators;
    }

    public HashSet<IrBasicBlock> GetDirectDominateBlocks() {
        return this.directDominateBlocks;
    }

    public IrBasicBlock GetDirectDominator() {
        return this.directDominator;
    }

    public HashSet<IrBasicBlock> GetDominateFrontiers() {
        return this.dominateFrontiers;
    }

    public void AddNextBlock(IrBasicBlock nextBlock) {
        if (!this.nextBlockList.contains(nextBlock)) {
            this.nextBlockList.add(nextBlock);
        }
    }

    public void AddBeforeBlock(IrBasicBlock beforeBlock) {
        if (!this.beforeBlockList.contains(beforeBlock)) {
            this.beforeBlockList.add(beforeBlock);
        }
    }

    public void ReplaceNextBlock(IrBasicBlock irBasicBlock) {
        this.nextBlockList.remove(irBasicBlock);
        this.nextBlockList.addAll(irBasicBlock.nextBlockList);
    }

    public void ReplaceBeforeBlock(IrBasicBlock irBasicBlock) {
        this.beforeBlockList.remove(irBasicBlock);
        this.beforeBlockList.addAll(irBasicBlock.beforeBlockList);
    }

    public void DeleteNextBlock(IrBasicBlock nextBlock) {
        this.nextBlockList.remove(nextBlock);
        nextBlock.beforeBlockList.remove(this);
    }

    public void AppendBlock(IrBasicBlock nextBlock) {
        // 对于原块的尾跳转
        Instr jumpInstr = this.GetLastInstr();
        jumpInstr.RemoveAllValueUse();
        this.instrList.remove(jumpInstr);
        // 添加下一个基本快的指令
        nextBlock.instrList.forEach(this::AddInstr);
        // 修改next信息
        this.ReplaceNextBlock(nextBlock);
        // 修改before信息
        for (IrBasicBlock nextNextBlock : nextBlock.nextBlockList) {
            nextNextBlock.ReplaceBeforeBlock(nextBlock);
            for (Instr instr : nextNextBlock.GetInstrList()) {
                if (instr instanceof PhiInstr phiInstr) {
                    phiInstr.ReplaceBlock(nextBlock, this);
                }
            }
        }
    }

    // 添加支配该block的block
    public void AddDominator(IrBasicBlock dominatorBlock) {
        this.dominators.add(dominatorBlock);
    }

    // 添加直接支配关系
    public void AddDirectDominatorRelationship(IrBasicBlock directDominator) {
        this.directDominator = directDominator;
        directDominator.directDominateBlocks.add(this);
    }

    // 添加支配边界信息
    public void AddDominateFrontier(IrBasicBlock frontierBlock) {
        this.dominateFrontiers.add(frontierBlock);
    }

    public static IrBasicBlock AddMiddleBlock(IrBasicBlock beforeBlock, IrBasicBlock nextBlock) {
        // 获取中间基本块
        IrBasicBlock middleBlock =
            IrBuilder.GetNewBasicBlockIr(beforeBlock.GetIrFunction(), nextBlock);
        // 修改跳转关系
        if (beforeBlock.GetLastInstr() instanceof JumpInstr jumpInstr) {
            jumpInstr.SetJumpTarget(middleBlock);
        } else if (beforeBlock.GetLastInstr() instanceof BranchInstr branchInstr) {
            if (branchInstr.GetTrueBlock() == nextBlock) {
                branchInstr.SetTrueBlock(middleBlock);
            } else if (branchInstr.GetFalseBlock() == nextBlock) {
                branchInstr.SetFalseBlock(middleBlock);
            }
        }
        // 给中间块创建跳转关系
        middleBlock.AddInstr(new JumpInstr(nextBlock, middleBlock));

        // 修改原先的流图信息，但是没有重建控制流！
        beforeBlock.nextBlockList.set(beforeBlock.nextBlockList.indexOf(nextBlock), middleBlock);
        nextBlock.beforeBlockList.set(nextBlock.beforeBlockList.indexOf(beforeBlock), middleBlock);
        middleBlock.beforeBlockList.add(beforeBlock);
        middleBlock.nextBlockList.add(nextBlock);

        return middleBlock;
    }

    // 清除活跃信息
    public void ClearActiveInfo() {
        this.defValueSet.clear();
        this.useValueSet.clear();
        this.inValueSet.clear();
        this.outValueSet.clear();
    }

    public HashSet<IrValue> GetDefValueSet() {
        return this.defValueSet;
    }

    public HashSet<IrValue> GetUseValueSet() {
        return this.useValueSet;
    }

    public HashSet<IrValue> GetInValueSet() {
        return this.inValueSet;
    }

    public HashSet<IrValue> GetOutValueSet() {
        return this.outValueSet;
    }

    public void SetInValueSet(HashSet<IrValue> inValueSet) {
        this.inValueSet = inValueSet;
    }

    public void SetOutValueSet(HashSet<IrValue> outValueSet) {
        this.outValueSet = outValueSet;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.irName);
        builder.append(":\n\t");
        builder.append(this.instrList.stream().map(Instr::toString).
            collect(Collectors.joining("\n\t")));
        return builder.toString();
    }

    @Override
    public void toMips() {
        new MipsLabel(this.GetMipsLabel());
        for (Instr instr : this.instrList) {
            instr.toMips();
        }
    }
}
