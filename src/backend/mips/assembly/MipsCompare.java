package backend.mips.assembly;

import backend.mips.Register;

public class MipsCompare extends MipsAssembly {
    public enum CompareType {
        SLT, SLE, SGT, SGE, SEQ, SNE
    }

    private final CompareType compareType;
    private final Register rd;
    private final Register rs;
    private final Register rt;

    public MipsCompare(CompareType compareType, Register rd, Register rs, Register rt) {
        this.compareType = compareType;
        this.rd = rd;
        this.rs = rs;
        this.rt = rt;
    }

    @Override
    public String toString() {
        return compareType.toString().toLowerCase() + " " + rd + " " + rs + " " + rt;
    }
}
