package midend.llvm.instr;

import midend.llvm.type.IrPointerType;
import midend.llvm.value.IrValue;

public class LoadInstr extends Instr {
    public LoadInstr(IrValue pointer) {
        super(((IrPointerType) pointer.GetIrType()).GetTargetType(), InstrType.LOAD);
        this.AddUseValue(pointer);
    }

    @Override
    public String toString() {
        IrValue pointer = this.GetPointer();
        return this.irName + " = load " + this.irType + ", " +
            pointer.GetIrType() + " " + pointer.GetIrName();
    }

    private IrValue GetPointer() {
        return this.useValueList.get(0);
    }
}
