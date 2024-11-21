package backend.mips.assembly;

import backend.mips.MipsBuilder;

public abstract class MipsAssembly {
    public MipsAssembly() {
        MipsBuilder.AddAssembly(this);
    }

    @Override
    public abstract String toString();
}
