package midend.llvm.instr.io;

import backend.mips.Register;
import backend.mips.assembly.MipsSyscall;
import backend.mips.assembly.fake.MarsLi;
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
    public boolean DefValue() {
        return false;
    }

    @Override
    public String toString() {
        IrValue printValue = this.GetPrintValue();
        return "call void @putch(i8 " + printValue.GetIrName() + ")";
    }

    @Override
    public void toMips() {
        super.toMips();

        IrValue printValue = this.GetPrintValue();
        this.LoadValueToRegister(printValue, Register.A0);

        new MarsLi(Register.V0, 11);
        new MipsSyscall();
    }
}
