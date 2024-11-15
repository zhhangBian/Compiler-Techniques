package midend.llvm.instr;

import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrValue;

public class ReturnInstr extends Instr {
    public ReturnInstr(String name, IrValue returnValue) {
        super(IrBaseType.VOID, name, InstrType.RETURN);
        if (returnValue != null) {
            this.AddUseValue(returnValue);
        }
    }

    public IrValue GetReturnValue() {
        return this.useValueList.isEmpty() ? null : this.useValueList.get(0);
    }
}
