package midend.llvm.instr.io;

import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrValue;

public class PrintCharInstr extends IoInstr {
    public PrintCharInstr(IrValue printValue) {
        super(IrBaseType.VOID);
        this.AddUseValue(printValue);
    }

    public IrValue GetPrintValue() {
        return this.useValueList.get(0);
    }

    public static String GetDeclare() {
        return "declare void @putch(i8)";
    }

    @Override
    public String toString() {
        IrValue printValue = this.GetPrintValue();
        return "call void @putch(i8 " + printValue.GetIrName() + ")";
    }
}
