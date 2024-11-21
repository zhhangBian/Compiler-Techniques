package backend.mips.assembly.data;

public class MipsWord extends MipsDataAssembly {
    private final String name;
    private final int value;

    public MipsWord(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String toString() {
        return this.name + ": .word " + this.value;
    }
}
