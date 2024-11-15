package midend.llvm.instr;

import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrValue;

/*
    store <ty> <value>, <ty>* <pointer>
    store i32 %2, i32* %1
 */
public class StoreInstr extends Instr {
    public StoreInstr(String name, IrValue fromValue, IrValue toValue) {
        super(IrBaseType.VOID, name, InstrType.STORE);
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
}
