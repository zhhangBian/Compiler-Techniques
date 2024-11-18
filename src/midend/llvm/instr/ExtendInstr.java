package midend.llvm.instr;

import midend.llvm.type.IrType;
import midend.llvm.value.IrValue;

public class ExtendInstr extends Instr {
    private final IrType targetType;

    public ExtendInstr(IrValue originValue, IrType targetType) {
        super(targetType, InstrType.EXTEND);
        this.targetType = targetType;
        this.AddUseValue(originValue);
        //throw new RuntimeException("debug");
    }

    @Override
    public String toString() {
        IrValue originValue = this.GetOriginValue();
        return this.irName + " = zext " + originValue.GetIrType() + " " +
            originValue.GetIrName() + " to " + this.targetType;
    }

    private IrValue GetOriginValue() {
        return this.useValueList.get(0);
    }
}
