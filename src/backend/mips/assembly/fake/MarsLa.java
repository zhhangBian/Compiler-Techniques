package backend.mips.assembly.fake;

import backend.mips.Register;

public class MarsLa extends MipsMars {
    private final Register target;
    private final String label;

    public MarsLa(Register target, String label) {
        this.target = target;
        this.label = label;
    }

    @Override
    public String toString() {
        return "la " + this.target + ", " + this.label;
    }
}
