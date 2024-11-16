package midend.llvm.instr;

import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrValue;

public class ReturnInstr extends Instr {
    public ReturnInstr(IrValue returnValue) {
        super(IrBaseType.VOID, InstrType.RETURN, "return");
        if (returnValue != null) {
            this.AddUseValue(returnValue);
        }
    }

    public IrValue GetReturnValue() {
        return this.useValueList.isEmpty() ? null : this.useValueList.get(0);
    }

    @Override
    public String toString() {
        IrValue returnValue = this.GetReturnValue();

        return returnValue == null ? "ret void" :
            "ret " + returnValue.GetIrType() + " " + returnValue.GetIrName();
    }
}
