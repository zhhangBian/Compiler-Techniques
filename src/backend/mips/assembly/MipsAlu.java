package backend.mips.assembly;

import backend.mips.Register;

public class MipsAlu extends MipsAssembly {
    public enum AluType {
        // calc_R
        ADD, SUB, ADDU, SUBU, AND, OR, NOR, XOR, SLT, SLTU,
        // shiftv
        SLLV, SRAV, SRLV,
        // calc_I
        ADDI, ADDIU, ANDI, ORI, XORI, SLTI, SLTIU,
        // shift
        SLL, SRA, SRL
    }

    private final AluType aluType;
    private final Register rd;
    private final Register rs;
    private final Register rt;
    private final Integer immediate;

    // calc_R、shift
    public MipsAlu(AluType aluType, Register rd, Register rs, Register rt) {
        super(MipsType.ALU);
        this.aluType = aluType;
        this.rd = rd;
        this.rs = rs;
        this.rt = rt;
        this.immediate = null;
    }

    // calc_I、shiftv
    public MipsAlu(AluType aluType, Register rd, Register rs, int number) {
        super(MipsType.ALU);
        this.aluType = aluType;
        this.rd = rd;
        this.rs = rs;
        this.rt = null;
        this.immediate = number;
    }

    @Override
    public String toString() {
        return this.immediate == null ?
            aluType.toString().toLowerCase() + " " + rd + " " + rs + " " + rt :
            aluType.toString().toLowerCase() + " " + rd + " " + rs + " " + immediate;
    }
}
