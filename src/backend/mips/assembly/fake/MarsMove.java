package backend.mips.assembly.fake;

import backend.mips.Register;

public class MarsMove extends MipsMars {
    private final Register dst;
    private final Register src;

    public MarsMove(Register dst, Register src) {
        this.dst = dst;
        this.src = src;
    }

    @Override
    public String toString() {
        return (this.dst == this.src ? "# " : "") +
            "move " + this.dst + " " + this.src;
    }
}
