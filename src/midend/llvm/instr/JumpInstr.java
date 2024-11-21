package midend.llvm.instr;

import backend.mips.assembly.MipsJump;
import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrBasicBlock;

public class JumpInstr extends Instr {
    public JumpInstr(IrBasicBlock targetBlock) {
        super(IrBaseType.VOID, InstrType.JUMP, "jump");
        this.AddUseValue(targetBlock);
    }

    public JumpInstr(IrBasicBlock targetBlock, boolean autoAdd) {
        super(IrBaseType.VOID, InstrType.JUMP, "jump", false);
        this.AddUseValue(targetBlock);
    }

    private IrBasicBlock GetTargetBlock() {
        return (IrBasicBlock) this.useValueList.get(0);
    }

    @Override
    public String toString() {
        IrBasicBlock targetBlock = this.GetTargetBlock();
        return "br label %" + targetBlock.GetIrName();
    }

    @Override
    public void toMips() {
        new MipsJump(MipsJump.JumpType.J, this.GetTargetBlock().GetMipsLabel());
    }
}
