package backend.mips.assembly.data;

import backend.mips.assembly.MipsAssembly;
import backend.mips.assembly.MipsType;

public abstract class MipsDataAssembly extends MipsAssembly {
    public MipsDataAssembly() {
        super(MipsType.DATA);
    }
}
