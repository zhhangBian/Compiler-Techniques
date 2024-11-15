package midend.llvm.value;

import midend.llvm.instr.Instr;
import midend.llvm.instr.ReturnInstr;
import midend.llvm.type.IrBasicBlockType;

import java.util.ArrayList;

public class IrBasicBlock extends IrValue {
    private final ArrayList<Instr> instrList;
    private IrFunction parentFunction;

    public IrBasicBlock(String irName) {
        super(IrBasicBlockType.BASIC_BLOCK, irName);
        this.instrList = new ArrayList<>();
        this.parentFunction = null;
    }

    public void SetParentFunction(IrFunction irFunction) {
        this.parentFunction = irFunction;
    }

    public void AddInstr(Instr instr) {
        this.instrList.add(instr);
    }

    public boolean LastInstrIsReturn() {
        if (this.instrList.isEmpty()) {
            return false;
        }
        return this.instrList.get(this.instrList.size() - 1) instanceof ReturnInstr;
    }
}
