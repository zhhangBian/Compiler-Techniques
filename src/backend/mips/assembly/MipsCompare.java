package backend.mips.assembly;

import backend.mips.Register;

public class MipsCompare extends MipsAssembly {
    public enum CompareType {
        SLT, SLE, SGT, SGE, SEQ, SNE, SLTI
    }

    private final CompareType compareType;
    private final Register rd;
    private final Register rs;
    private final Register rt;
    private final Integer immediate;

    public MipsCompare(CompareType compareType, Register rd, Register rs, Register rt) {
        super(MipsType.COMPARE);
        this.compareType = compareType;
        this.rd = rd;
        this.rs = rs;
        this.rt = rt;
        this.immediate = null;
    }

    public MipsCompare(CompareType compareType, Register rd, Register rs, Integer immediate) {
        super(MipsType.COMPARE);
        this.compareType = compareType;
        this.rd = rd;
        this.rs = rs;
        this.rt = null;
        this.immediate = immediate;
    }

    @Override
    public String toString() {
        return this.immediate == null ?
            compareType.toString().toLowerCase() + " " + rd + " " + rs + " " + rt :
            compareType.toString().toLowerCase() + " " + rd + " " + rs + " " + immediate;
    }
}
