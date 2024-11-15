package midend.llvm.instr.io;

import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrValue;

public class PrintCharInstr extends IoInstr {
    public PrintCharInstr(String irName, IrValue printValue) {
        super(IrBaseType.VOID, irName);
        this.AddUseValue(printValue);
    }

    public IrValue GetPrintValue() {
        return this.useValueList.get(0);
    }

    @Override
    public String GetDeclare() {
        return "declare void @putchar(i8)";
    }

    @Override
    public String toString() {
        IrValue printValue = this.GetPrintValue();
        return "call void @putchar(i8 " + printValue.GetIrName() + ")";
    }
}
