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
    public StoreInstr(IrValue fromValue, IrValue toValue) {
        super(IrBaseType.VOID, InstrType.STORE, "store");
        this.AddUseValue(fromValue);
        this.AddUseValue(toValue);
    }

    public IrValue GetFromValue() {
        return this.useValueList.get(0);
    }

    public IrValue GetToValue() {
        return this.useValueList.get(1);
    }

    @Override
    public String toString() {
        IrValue fromValue = this.GetFromValue();
        IrValue toValue = this.GetToValue();
        return "store " +
            fromValue.GetIrType() + " " + fromValue.GetIrName() + ", " +
            toValue.GetIrType() + " " + toValue.GetIrName();
    }

    @Override
    public void toMips() {
        IrValue fromValue = this.GetFromValue();
        IrValue toValue = this.GetToValue();

        Register fromRegister = Register.K0;
        Register toRegister = Register.K1;

        // 获取store的值
        this.LoadValueToRegister(fromValue, fromRegister);
        // 获取store的地址
        this.LoadValueToRegister(toValue, toRegister);

        new MipsLsu(MipsLsu.LsuType.SW, fromRegister, toRegister, 0);
    }
}
