package backend.mips.assembly;

public class MipsAnnotation extends MipsAssembly {
    private final String annotation;

    public MipsAnnotation(String annotation) {
        super(MipsType.ANNOTATION);
        this.annotation = annotation;
    }

    @Override
    public String toString() {
        return "\n# " + this.annotation;
    }
}
