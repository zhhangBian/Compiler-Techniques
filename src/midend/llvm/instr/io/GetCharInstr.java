package midend.llvm.instr.io;

import midend.llvm.type.IrBaseType;

public class GetCharInstr extends IoInstr {
    public GetCharInstr(String irName) {
        super(IrBaseType.INT8, irName);
    }

    @Override
    public String GetDeclare() {
        return "declare i8 @getchar(...) ";
    }

    @Override
    public String toString() {
        return this.irName + " = call i8 (...) @getint()";
    }
}
