package backend.mips.assembly;

import backend.mips.Register;

public class MipsJump extends MipsAssembly {
    public enum JumpType {
        J, JAL, JR
    }

    private final JumpType jumpType;
    private final String targetLabel;
    private final Register rd;

    public MipsJump(JumpType jumpType, String targetLabel) {
        super(MipsType.JUMP);
        this.jumpType = jumpType;
        this.targetLabel = targetLabel;
        this.rd = null;
    }

    public MipsJump(JumpType jumpType, Register rd) {
        super(MipsType.JUMP);
        this.jumpType = jumpType;
        this.targetLabel = null;
        this.rd = rd;
    }

    @Override
    public String toString() {
        return switch (this.jumpType) {
            case JR -> jumpType.toString().toLowerCase() + " " + rd;
            case J, JAL -> jumpType.toString().toLowerCase() + " " + targetLabel;
            default -> throw new RuntimeException("illegal mips jump type");
        };
    }
}
