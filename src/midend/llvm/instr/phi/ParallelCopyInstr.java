package midend.llvm.instr.phi;

import midend.llvm.instr.Instr;
import midend.llvm.instr.InstrType;
import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrValue;

import java.util.ArrayList;

public class ParallelCopyInstr extends Instr {
    private final ArrayList<IrValue> srcList;
    private final ArrayList<IrValue> dstList;

    public ParallelCopyInstr(IrBasicBlock irBasicBlock) {
        super(IrBaseType.VOID, InstrType.PCOPY, "parallel-copy", false);
        this.srcList = new ArrayList<>();
        this.dstList = new ArrayList<>();
        this.SetInBasicBlock(irBasicBlock);
    }

    public void AddCopy(IrValue src, IrValue dst) {
        this.srcList.add(src);
        this.dstList.add(dst);
    }

    public ArrayList<IrValue> GetSrcList() {
        return this.srcList;
    }

    public ArrayList<IrValue> GetDstList() {
        return this.dstList;
    }

    @Override
    public boolean DefValue() {
        return false;
    }

    @Override
    public String toString() {
        return "parallel-copy-instr";
    }

    @Override
    public void toMips() {
        throw new RuntimeException("pcopy not delete!");
    }
}
