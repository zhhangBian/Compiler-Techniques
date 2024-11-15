package midend.llvm.instr.io;

import midend.llvm.instr.Instr;
import midend.llvm.instr.InstrType;
import midend.llvm.type.IrType;

public abstract class IoInstr extends Instr {
    public IoInstr(IrType irType, String name) {
        super(irType, name, InstrType.IO);
    }

    public abstract String GetDeclare();

    @Override
    public abstract String toString();
}
