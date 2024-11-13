package midend.llvm.instr;

import midend.llvm.type.IrType;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrUser;

public class Instr extends IrUser {
    private InstrType instrType;
    private IrBasicBlock basicBlock;

    public Instr(IrType irType, String name) {
        super(irType, name);
    }
}
