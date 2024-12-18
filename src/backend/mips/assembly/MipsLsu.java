package backend.mips.assembly;

import backend.mips.Register;

public class MipsLsu extends MipsAssembly {
    public enum LsuType {
        // load
        LW, LH, LHU, LB, LBU,
        // store
        SW, SH, SB
    }

    private final LsuType lsuType;
    private final Register rd;
    private final Register base;
    private final Integer offset;
    private final String label;

    public MipsLsu(LsuType lsuType, Register rd, Register base, Integer offset) {
        super(MipsType.LSU);
        this.lsuType = lsuType;
        this.rd = rd;
        this.base = base;
        this.offset = offset;
        this.label = null;
    }

    public MipsLsu(LsuType lsuType, Register rd, String label, Integer offset) {
        super(MipsType.LSU);
        this.lsuType = lsuType;
        this.rd = rd;
        this.base = null;
        this.offset = offset;
        this.label = label;
    }

    public boolean IsStoreType() {
        return switch (this.lsuType) {
            case SW, SH, SB -> true;
            default -> false;
        };
    }

    public String GetTarget() {
        return this.label == null ? offset + "(" + base + ")" : label + "+" + offset;
    }

    @Override
    public String toString() {
        return this.label == null ?
            lsuType.toString().toLowerCase() + " " + rd + " " + offset + "(" + base + ")" :
            lsuType.toString().toLowerCase() + " " + rd + " " + label + "+" + offset;
    }
}
