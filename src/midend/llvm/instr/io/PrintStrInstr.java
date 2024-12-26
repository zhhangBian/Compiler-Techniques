package midend.llvm.instr.io;

import backend.mips.Register;
import backend.mips.assembly.MipsSyscall;
import backend.mips.assembly.fake.MarsLa;
import backend.mips.assembly.fake.MarsLi;
import midend.llvm.constant.IrConstantString;
import midend.llvm.type.IrBaseType;
import midend.llvm.type.IrPointerType;
import midend.llvm.value.IrValue;

public class PrintStrInstr extends IoInstr {
    private final IrConstantString irConstantString;
    private final IrValue stringAddress;

    public PrintStrInstr(IrConstantString irConstantString) {
        super(IrBaseType.VOID);
        this.irConstantString = irConstantString;
        this.stringAddress = null;
    }

    public PrintStrInstr(IrValue stringAddress, Object object) {
        super(IrBaseType.VOID);
        this.irConstantString = null;
        this.stringAddress = stringAddress;
    }

    public static String GetDeclare() {
        return "declare void @putstr(i8*)";
    }

    @Override
    public boolean DefValue() {
        return false;
    }

    @Override
    public String toString() {
        if (this.irConstantString != null) {
            IrPointerType irPointerType = (IrPointerType) this.irConstantString.GetIrType();
            return "call void @putstr(i8* getelementptr inbounds (" +
                irPointerType.GetTargetType() + ", " +
                irPointerType + " " +
                this.irConstantString.GetIrName() + ", i64 0, i64 0))";
        } else {
            return "print str";
            //return "call void @putstr(i8* getelementptr inbounds (" +
            //    irPointerType.GetTargetType() + ", " +
            //    irPointerType + " " +
            //    this.irConstantString.GetIrName() + ", i64 0, i64 0))";
        }
    }

    @Override
    public void toMips() {
        super.toMips();

        if (this.irConstantString != null) {
            new MarsLa(Register.A0, this.irConstantString.GetMipsLabel());
        } else {
            this.LoadValueToRegister(stringAddress, Register.A0);
        }

        new MarsLi(Register.V0, 4);
        new MipsSyscall();
    }
}
