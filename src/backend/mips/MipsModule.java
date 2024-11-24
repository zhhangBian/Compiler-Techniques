package backend.mips;

import backend.mips.assembly.MipsAssembly;

import java.util.ArrayList;

public class MipsModule {
    private final ArrayList<MipsAssembly> dataSegment;
    private final ArrayList<MipsAssembly> textSegment;

    public MipsModule() {
        this.dataSegment = new ArrayList<>();
        this.textSegment = new ArrayList<>();
    }

    public void AddToData(MipsAssembly mipsAssembly) {
        this.dataSegment.add(mipsAssembly);
    }

    public void AddToText(MipsAssembly mipsAssembly) {
        this.textSegment.add(mipsAssembly);
    }

    public ArrayList<MipsAssembly> GetTextSegment() {
        return this.textSegment;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        // data
        builder.append(".data\n");
        for (MipsAssembly dataAssembly : this.dataSegment) {
            builder.append("\t");
            builder.append(dataAssembly);
            builder.append("\n");
        }
        builder.append("\n\n");

        // text
        builder.append(".text\n");
        for (MipsAssembly textAssembly : this.textSegment) {
            builder.append("\t");
            builder.append(textAssembly);
            builder.append("\n");
        }

        return builder.toString();
    }
}
