package midend.llvm.instr;

import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrValue;

public class BranchInstr extends Instr {
    public BranchInstr(IrValue cond, IrBasicBlock trueBlock, IrBasicBlock falseBlock) {
        super(IrBaseType.VOID, InstrType.BRANCH);
        this.useValueList.add(cond);
        this.useValueList.add(trueBlock);
        this.useValueList.add(falseBlock);
    }

    private IrValue GetCond() {
        return this.useValueList.get(0);
    }

    private IrBasicBlock GetTrueBlock() {
        return (IrBasicBlock) this.useValueList.get(1);
    }

    private IrBasicBlock GetFalseBlock() {
        return (IrBasicBlock) this.useValueList.get(2);
    }

    @Override
    public String toString() {
        IrValue cond = this.GetCond();
        IrBasicBlock trueBlock = this.GetTrueBlock();
        IrBasicBlock falseBlock = this.GetFalseBlock();

        return "br i1 " + cond.GetIrName() +
            ", label %" + trueBlock.GetIrName() +
            ", label %" + falseBlock.GetIrName();
    }
}
