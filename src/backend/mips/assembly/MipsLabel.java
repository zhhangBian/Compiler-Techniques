package backend.mips.assembly;

public class MipsLabel extends MipsAssembly {
    private final String label;

    public MipsLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label + ":";
    }
}
