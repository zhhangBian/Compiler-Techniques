package backend.mips.assembly.data;

public class MipsAsciiz extends MipsDataAssembly {
    private final String name;
    private final String content;

    public MipsAsciiz(String name, String content) {
        this.name = name;
        this.content = content;
    }

    @Override
    public String toString() {
        return this.name + ": .asciiz \"" +
            this.content.replace("\n", "\\n") + "\"";
    }
}
