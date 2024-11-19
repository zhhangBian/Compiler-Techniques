package backend.mips.assembly.fake;

import backend.mips.Register;

public class MarsLi extends MipsMars {
    private final Register rd;
    private final Integer number;

    public MarsLi(Register rd, Integer number) {
        this.rd = rd;
        this.number = number;
    }

    @Override
    public String toString() {
        return "li " + this.rd + " " + this.number;
    }
}
