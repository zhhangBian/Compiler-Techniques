package midend.llvm.instr;

import midend.llvm.type.IrType;
import midend.llvm.value.IrValue;

public class TruncInstr extends Instr {
    private final IrType targetType;

    public TruncInstr(IrValue originValue, IrType targetType) {
        super(targetType, InstrType.EXTEND);
        this.targetType = targetType;
        this.AddUseValue(originValue);
    }

    @Override
    public String toString() {
        IrValue originValue = this.GetOriginValue();
        return this.irName + " = trunc " + originValue.GetIrType() + " " +
            originValue.GetIrName() + " to " + this.targetType;
    }

    private IrValue GetOriginValue() {
        return this.useValueList.get(0);
    }
}
