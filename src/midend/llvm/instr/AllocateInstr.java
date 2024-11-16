package midend.llvm.instr;

import midend.llvm.type.IrPointerType;
import midend.llvm.type.IrType;

public class AllocateInstr extends Instr {
    private final IrType targetType;

    public AllocateInstr(IrType targetType) {
        super(new IrPointerType(targetType), InstrType.ALLOCATE);
        this.targetType = targetType;
    }

    @Override
    public String toString() {
        return this.irName + " = alloca " + this.targetType;
    }
}
