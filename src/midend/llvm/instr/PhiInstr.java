package midend.llvm.instr;

import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrBasicBlock;

import java.util.ArrayList;

public class PhiInstr extends Instr {
    private final ArrayList<IrBasicBlock> parentBlockList;

    public PhiInstr(ArrayList<IrBasicBlock> parentBlockList) {
        super(IrBaseType.INT32, InstrType.PHI);
        this.parentBlockList = parentBlockList;
    }

    @Override
    public String toString() {
        throw new RuntimeException("phi not finished yet");
    }
}
