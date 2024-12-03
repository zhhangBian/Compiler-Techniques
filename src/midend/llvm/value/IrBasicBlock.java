package midend.llvm.value;

import backend.mips.assembly.MipsLabel;
import midend.llvm.instr.Instr;
import midend.llvm.instr.ReturnInstr;
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

    // 直接支配该block的block
    private IrBasicBlock immediateDominator;
    // 支配该block的block
    private final HashSet<IrBasicBlock> dominators;
    // 该block直接支配的block
    private final HashSet<IrBasicBlock> immediateDominaters;
    // 该block的支配边界
    private final HashSet<IrBasicBlock> dominateFrontiers;


    public IrBasicBlock(String irName, IrFunction irFunction) {
        super(IrBasicBlockType.BASIC_BLOCK, irName);
        this.instrList = new ArrayList<>();
        this.irFunction = irFunction;
        // 描述前驱后继的数据结构
        this.nextBlockList = new ArrayList<>();
        this.beforeBlockList = new ArrayList<>();
        // 描述支配关系的数据结构
        this.immediateDominator = null;
        this.dominators = new HashSet<>();
        this.immediateDominaters = new HashSet<>();
        this.dominateFrontiers = new HashSet<>();
    }

    public boolean IsEmptyBlock() {
        return this.instrList.isEmpty();
    }

    public void AddInstr(Instr instr) {
        this.instrList.add(instr);
    }

    public ArrayList<Instr> GetInstrList() {
        return this.instrList;
    }

    public Instr GetLastInstr() {
        return this.instrList.get(this.instrList.size() - 1);
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

    public void ClearCfg() {
        // 可达关系
        this.nextBlockList.clear();
        this.beforeBlockList.clear();
        // 支配关系
        this.immediateDominator = null;
        this.dominators.clear();
        this.immediateDominaters.clear();
        this.dominateFrontiers.clear();
    }

    public void AddNextBlock(IrBasicBlock nextBlock) {
        this.nextBlockList.add(nextBlock);
    }

    public void AddBeforeBlock(IrBasicBlock beforeBlock) {
        this.beforeBlockList.add(beforeBlock);
    }

    // 添加支配该block的block
    public void AddDominator(IrBasicBlock dominatorBlock) {
        this.dominators.add(dominatorBlock);
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
