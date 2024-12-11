package midend.llvm.instr;

import backend.mips.Register;
import midend.llvm.type.IrType;
import midend.llvm.value.IrValue;

public class ExtendInstr extends Instr {
    private final IrType targetType;

    public ExtendInstr(IrValue originValue, IrType targetType) {
        super(targetType, InstrType.EXTEND);
        this.targetType = targetType;
        this.AddUseValue(originValue);
    }

    public IrType GetTargetType() {
        return this.targetType;
    }

    public IrType GetOriginType() {
        return this.GetOriginValue().GetIrType();
    }

    public IrValue GetOriginValue() {
        return this.useValueList.get(0);
    }

    @Override
    public boolean DefValue() {
        return true;
    }

    @Override
    public String toString() {
        IrValue originValue = this.GetOriginValue();
        return this.irName + " = zext " + originValue.GetIrType() + " " +
            originValue.GetIrName() + " to " + this.targetType;
    }

    @Override
    public void toMips() {
        super.toMips();

        // mips不需要位宽扩展，只需要将值进行映射，使得使用this的指令能使用到origin
        IrValue originValue = this.GetOriginValue();
        Register register = this.GetRegisterOrK0ForValue(this);
        this.LoadValueToRegister(originValue, register);
        this.SaveRegisterResult(this, register);
    }
}
