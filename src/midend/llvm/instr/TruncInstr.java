package midend.llvm.instr;

import backend.mips.Register;
import backend.mips.assembly.MipsAlu;
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

    @Override
    public void toMips() {
        super.toMips();

        // 缩减即防止溢出，缩减为两位
        IrValue originValue = this.GetOriginValue();
        Register valueRegister = this.GetRegisterOrK0ForValue(originValue);
        this.LoadValueToRegister(originValue, valueRegister);
        new MipsAlu(MipsAlu.AluType.ANDI, valueRegister, valueRegister, 0xff);
        this.SaveResult(this, valueRegister);
    }

    private IrValue GetOriginValue() {
        return this.useValueList.get(0);
    }
}
