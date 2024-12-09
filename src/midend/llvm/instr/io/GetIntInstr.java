package midend.llvm.instr.io;

import backend.mips.Register;
import backend.mips.assembly.MipsSyscall;
import backend.mips.assembly.fake.MarsLi;
import midend.llvm.type.IrBaseType;

public class GetIntInstr extends IoInstr {
    public GetIntInstr() {
        super(IrBaseType.INT32);
    }

    public static String GetDeclare() {
        return "declare i32 @getint()";
    }

    @Override
    public boolean DefValue() {
        return true;
    }

    @Override
    public String toString() {
        return this.irName + " = call i32 @getint()";
    }

    @Override
    public void toMips() {
        super.toMips();

        new MarsLi(Register.V0, 5);
        new MipsSyscall();
        this.SaveRegisterResult(this, Register.V0);
    }
}
