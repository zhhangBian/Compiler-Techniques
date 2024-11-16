package midend.llvm.instr.io;

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
    public String toString() {
        IrPointerType irPointerType = (IrPointerType) irConstantString.GetIrType();
        return "call void @putstr(i8* getelementptr inbounds (" +
            irPointerType.GetTargetType() + ", " +
            irPointerType + " " +
            irConstantString.GetIrName() + ", i64 0, i64 0))";
    }
}
