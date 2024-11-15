package midend.llvm.instr;

import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrValue;

public class ExtendInstr extends Instr {
    private final IrBaseType targetType;

    public ExtendInstr(String irName, IrValue originValue, IrBaseType targetType) {
        super(targetType, irName, InstrType.EXTEND);
        this.targetType = targetType;
        this.AddUseValue(originValue);
    }

    @Override
    public String toString() {
        IrValue originValue = this.GetOriginValue();
        return "zext " + originValue.GetIrType() + " " +
            originValue.GetIrName() + " to " + this.targetType;
    }

    private IrValue GetOriginValue() {
        return this.useValueList.get(0);
    }
}
