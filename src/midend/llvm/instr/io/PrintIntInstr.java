package midend.llvm.instr.io;

import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrValue;

public class PrintIntInstr extends IoInstr {
    public PrintIntInstr(String irName, IrValue printValue) {
        super(IrBaseType.VOID, irName);
        this.AddUseValue(printValue);
    }

    public IrValue GetPrintValue() {
        return this.useValueList.get(0);
    }

    @Override
    public String GetDeclare() {
        return "declare void @putint(i32)";
    }

    @Override
    public String toString() {
        IrValue printValue = this.GetPrintValue();
        return "call void @putint(i32 " + printValue.GetIrName() + ")";
    }
}
