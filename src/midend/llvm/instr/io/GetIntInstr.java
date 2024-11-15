package midend.llvm.instr.io;

import midend.llvm.type.IrBaseType;

public class GetIntInstr extends IoInstr {
    public GetIntInstr(String irName) {
        super(IrBaseType.INT32, irName);
    }

    public static String GetDeclare() {
        return "declare i32 @getint()";
    }

    @Override
    public String toString() {
        return this.irName + " = call i32 (...) @getint()";
    }
}
