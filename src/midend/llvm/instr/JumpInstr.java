package midend.llvm.instr;

import backend.mips.assembly.MipsJump;
import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrBasicBlock;

public class JumpInstr extends Instr {
    public JumpInstr(IrBasicBlock targetBlock) {
        super(IrBaseType.VOID, InstrType.JUMP, "jump");
        this.AddUseValue(targetBlock);
    }

    public JumpInstr(IrBasicBlock targetBlock, IrBasicBlock createBlock) {
        super(IrBaseType.VOID, InstrType.JUMP, "jump", false);
        this.AddUseValue(targetBlock);
        this.SetInBasicBlock(createBlock);
    }

    public void SetJumpTarget(IrBasicBlock targetBlock) {
        // 删除原先的使用关系
        this.GetTargetBlock().DeleteUser(this);
        this.useValueList.clear();
        this.AddUseValue(targetBlock);
    }

    public IrBasicBlock GetTargetBlock() {
        return (IrBasicBlock) this.useValueList.get(0);
    }

    @Override
    public boolean DefValue() {
        return false;
    }

    @Override
    public String toString() {
        IrBasicBlock targetBlock = this.GetTargetBlock();
        return "br label %" + targetBlock.GetIrName();
    }

    @Override
    public void toMips() {
        super.toMips();

        new MipsJump(MipsJump.JumpType.J, this.GetTargetBlock().GetMipsLabel());
    }
}
