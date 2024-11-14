package midend.llvm.instr;

import midend.llvm.type.IrType;

public class AluInstr extends Instr {
    public enum Type {
        ADD,
        SUB,
        AND,
        OR,
        MUL,
        DIV
    }

    private Type aluType;

    public AluInstr(IrType irType, String name) {
        super(irType, name);
    }
}
