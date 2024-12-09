package midend.llvm.instr.io;

import backend.mips.Register;
import backend.mips.assembly.MipsSyscall;
import backend.mips.assembly.fake.MarsLi;
import midend.llvm.type.IrBaseType;

public class GetCharInstr extends IoInstr {
    public GetCharInstr() {
        super(IrBaseType.INT32);
    }

    public static String GetDeclare() {
        return "declare i32 @getchar() ";
    }

    @Override
    public boolean DefValue() {
        return true;
    }

    @Override
    public String toString() {
        return this.irName + " = call i32 @getchar()";
    }

    @Override
    public void toMips() {
        super.toMips();

        new MarsLi(Register.V0, 12);
        new MipsSyscall();
        this.SaveRegisterResult(this, Register.V0);
    }
}
