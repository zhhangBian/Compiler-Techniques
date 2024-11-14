package midend.llvm.instr;

import midend.llvm.type.IrType;

public class AluInstr extends Instr {
    public enum AluType {
        ADD,
        SUB,
        AND,
        OR,
        MUL,
        DIV
    }

    private final AluType aluType;

    public AluInstr(IrType irType, String name, AluType aluType) {
        super(irType, name, InstrType.ALU);
        this.aluType = aluType;
    }
}
