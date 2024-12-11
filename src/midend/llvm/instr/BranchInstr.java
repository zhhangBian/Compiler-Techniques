package midend.llvm.instr;

import backend.mips.Register;
import backend.mips.assembly.MipsBranch;
import backend.mips.assembly.MipsJump;
import midend.llvm.type.IrBaseType;
import midend.llvm.use.IrUse;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrValue;

public class BranchInstr extends Instr {
    public BranchInstr(IrValue cond, IrBasicBlock trueBlock, IrBasicBlock falseBlock) {
        super(IrBaseType.VOID, InstrType.BRANCH);
        this.AddUseValue(cond);
        this.AddUseValue(trueBlock);
        this.AddUseValue(falseBlock);
    }

    public IrValue GetCond() {
        return this.useValueList.get(0);
    }

    public void SetTrueBlock(IrBasicBlock trueBlock) {
        this.GetTrueBlock().DeleteUser(this);
        this.useValueList.set(1, trueBlock);
        trueBlock.AddUse(new IrUse(this, trueBlock));
    }

    public void SetFalseBlock(IrBasicBlock falseBlock) {
        this.GetTrueBlock().DeleteUser(this);
        this.useValueList.add(2, falseBlock);
        falseBlock.AddUse(new IrUse(this, falseBlock));
    }

    public IrBasicBlock GetTrueBlock() {
        return (IrBasicBlock) this.useValueList.get(1);
    }

    public IrBasicBlock GetFalseBlock() {
        return (IrBasicBlock) this.useValueList.get(2);
    }

    @Override
    public boolean DefValue() {
        return false;
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
        super.toMips();

        IrValue cond = this.GetCond();
        Register condRegister = this.GetRegisterOrK0ForValue(cond);
        this.LoadValueToRegister(cond, condRegister);
        // 利用bne进行cond判断
        new MipsBranch(MipsBranch.BranchType.BNE, condRegister, Register.ZERO,
            this.GetTrueBlock().GetMipsLabel());
        // 否则进入分支
        new MipsJump(MipsJump.JumpType.J, this.GetFalseBlock().GetMipsLabel());
    }
}
