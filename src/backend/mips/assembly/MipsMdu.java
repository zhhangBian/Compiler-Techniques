package backend.mips.assembly;

import backend.mips.Register;

public class MipsMdu extends MipsAssembly {
    public enum MduType {
        MFHI, MFLO, MTHI, MTLO
    }

    private final MduType mduType;
    private final Register rd;

    public MipsMdu(MduType mduType, Register rd) {
        this.mduType = mduType;
        this.rd = rd;
    }

    @Override
    public String toString() {
        return this.mduType.toString().toLowerCase() + " " + this.rd;
    }
}
