package backend.mips.assembly;

import backend.mips.Register;

public class MipsBranch extends MipsAssembly {
    public enum BranchType {
        BEQ, BNE, BGTZ, BLEZ, BGEZ, BLTZ
    }

    private final BranchType branchType;
    private final Register rs;
    private final Register rt;
    private final String label;

    // beq, bne
    public MipsBranch(BranchType branchType, Register rs, Register rt, String label) {
        super(MipsType.BRANCH);
        this.branchType = branchType;
        this.rs = rs;
        this.rt = rt;
        this.label = label;
    }

    // bgtz, bgez, bltz, blez
    public MipsBranch(BranchType branchType, Register rs, String label) {
        super(MipsType.BRANCH);
        this.branchType = branchType;
        this.rs = rs;
        this.rt = null;
        this.label = label;
    }

    @Override
    public String toString() {
        return switch (this.branchType) {
            case BEQ, BNE ->
                branchType.toString().toLowerCase() + " " + rs + " " + rt + " " + label;
            case BGTZ, BLEZ, BGEZ, BLTZ ->
                branchType.toString().toLowerCase() + " " + rs + " " + label;
            default -> throw new RuntimeException("illegal mips branch type");
        };
    }
}
