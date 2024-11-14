package midend.llvm.instr;

import midend.llvm.type.IrPointerType;
import midend.llvm.type.IrType;

public class AllocateInstr extends Instr {
    public AllocateInstr(String name, IrType targetType) {
        super(new IrPointerType(targetType), name, InstrType.ALLOCATE);
    }
}
