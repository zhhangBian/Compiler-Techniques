package backend.mips.assembly;

import backend.mips.Register;

public class MipsMdu extends MipsAssembly {
    public enum MduType {
        MFHI, MFLO, MTHI, MTLO,
        MULT, DIV
    }

    private final MduType mduType;
    private final Register rd;
    private final Register rs;
    private final Register rt;

    public MipsMdu(MduType mduType, Register rd) {
        this.mduType = mduType;
        this.rd = rd;
        this.rs = null;
        this.rt = null;
    }

    public MipsMdu(MduType mduType, Register rs, Register rt) {
        this.mduType = mduType;
        this.rd = null;
        this.rs = rs;
        this.rt = rt;
    }

    @Override
    public String toString() {
        return this.rd == null ?
            this.mduType.toString().toLowerCase() + " " + rs + " " + rt :
            this.mduType.toString().toLowerCase() + " " + this.rd;
    }
}
