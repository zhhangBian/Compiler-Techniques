package midend.llvm.value;

import backend.mips.assembly.MipsLabel;
import midend.llvm.instr.Instr;
import midend.llvm.instr.ReturnInstr;
import midend.llvm.type.IrBasicBlockType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class IrBasicBlock extends IrValue {
    private final ArrayList<Instr> instrList;
    private final IrFunction irFunction;

    public IrBasicBlock(String irName, IrFunction irFunction) {
        super(IrBasicBlockType.BASIC_BLOCK, irName);
        this.instrList = new ArrayList<>();
        this.irFunction = irFunction;
    }

    public boolean IsEmptyBlock() {
        return this.instrList.isEmpty();
    }

    public void AddInstr(Instr instr) {
        this.instrList.add(instr);
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
