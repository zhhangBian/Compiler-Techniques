package midend.llvm.instr;

import backend.mips.MipsBuilder;
import backend.mips.Register;
import backend.mips.assembly.MipsBranch;
import backend.mips.assembly.MipsJump;
import backend.mips.assembly.MipsLsu;
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

    @Override
    public void toMips() {
        IrValue cond = this.GetCond();
        Register condRegister = MipsBuilder.GetValueToRegister(cond);

        if (condRegister == null) {
            condRegister = Register.K0;
            new MipsLsu(MipsLsu.LsuType.LW, condRegister, Register.SP,
                MipsBuilder.GetStackValueOffset(cond));
        }

        // 利用bne进行cond判断
        new MipsBranch(MipsBranch.BranchType.BNE, condRegister, Register.ZERO,
            this.GetTrueBlock().GetMipsLabel());
        // 否则进入分支
        new MipsJump(MipsJump.JumpType.J, this.GetFalseBlock().GetMipsLabel());
    }
}
