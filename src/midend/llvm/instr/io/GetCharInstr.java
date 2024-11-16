package midend.llvm.instr.io;

import midend.llvm.type.IrBaseType;

public class GetCharInstr extends IoInstr {
    public GetCharInstr() {
        super(IrBaseType.INT8);
    }

    public static String GetDeclare() {
        return "declare i32 @getchar() ";
    }

    @Override
    public String toString() {
        return this.irName + " = call i8 @getchar()";
    }
}
