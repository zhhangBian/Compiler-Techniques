package backend.mips.assembly;

public class MipsAnnotation extends MipsAssembly {
    private final String annotation;

    public MipsAnnotation(String annotation) {
        this.annotation = annotation;
    }


    @Override
    public String toString() {
        return "# " + this.annotation;
    }
}
