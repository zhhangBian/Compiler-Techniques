package midend.llvm.instr;

import backend.mips.Register;
import backend.mips.assembly.MipsLsu;
import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrValue;

/*
    store <ty> <value>, <ty>* <pointer>
    store i32 %2, i32* %1
 */
public class StoreInstr extends Instr {
    public StoreInstr(IrValue valueValue, IrValue addressValue) {
        super(IrBaseType.VOID, InstrType.STORE, "store");
        this.AddUseValue(valueValue);
        this.AddUseValue(addressValue);
    }

    public IrValue GetValueValue() {
        return this.useValueList.get(0);
    }

    public IrValue GetAddressValue() {
        return this.useValueList.get(1);
    }

    @Override
    public boolean DefValue() {
        return false;
    }

    @Override
    public String toString() {
        IrValue valueValue = this.GetValueValue();
        IrValue addressValue = this.GetAddressValue();
        return "store " +
            valueValue.GetIrType() + " " + valueValue.GetIrName() + ", " +
            addressValue.GetIrType() + " " + addressValue.GetIrName();
    }

    @Override
    public void toMips() {
        super.toMips();

        IrValue valueValue = this.GetValueValue();
        IrValue addressValue = this.GetAddressValue();

        Register valueRegister = this.GetRegisterOrK0ForValue(valueValue);
        Register addressRegister = this.GetRegisterOrK1ForValue(addressValue);

        // 获取value的值
        this.LoadValueToRegister(valueValue, valueRegister);
        // 获取address的值
        this.LoadValueToRegister(addressValue, addressRegister);

        new MipsLsu(MipsLsu.LsuType.SW, valueRegister, addressRegister, 0);
    }
}
