package midend.llvm.instr.io;

import backend.mips.Register;
import backend.mips.assembly.MipsSyscall;
import backend.mips.assembly.fake.MarsLa;
import backend.mips.assembly.fake.MarsLi;
import midend.llvm.constant.IrConstantString;
import midend.llvm.type.IrBaseType;
import midend.llvm.type.IrPointerType;

public class PrintStrInstr extends IoInstr {
    private final IrConstantString irConstantString;

    public PrintStrInstr(IrConstantString irConstantString) {
        super(IrBaseType.VOID);
        this.irConstantString = irConstantString;
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
        IrPointerType irPointerType = (IrPointerType) this.irConstantString.GetIrType();
        return "call void @putstr(i8* getelementptr inbounds (" +
            irPointerType.GetTargetType() + ", " +
            irPointerType + " " +
            this.irConstantString.GetIrName() + ", i64 0, i64 0))";
    }

    @Override
    public void toMips() {
        super.toMips();

        new MarsLa(Register.A0, this.irConstantString.GetMipsLabel());
        new MarsLi(Register.V0, 4);
        new MipsSyscall();
    }
}
