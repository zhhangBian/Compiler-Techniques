package midend.llvm.instr;

import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrBasicBlock;

public class JumpInstr extends Instr {
    public JumpInstr(IrBasicBlock targetBlock) {
        super(IrBaseType.VOID, InstrType.JUMP, "jump");
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
}
