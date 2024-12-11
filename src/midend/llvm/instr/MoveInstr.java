package midend.llvm.instr;

import backend.mips.MipsBuilder;
import backend.mips.Register;
import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrValue;

public class MoveInstr extends Instr {
    public MoveInstr(IrValue srcValue, IrValue dstValue, IrBasicBlock irBasicBlock) {
        super(IrBaseType.VOID, InstrType.MOVE, "move", false);
        this.AddUseValue(srcValue);
        this.AddUseValue(dstValue);
        this.SetInBasicBlock(irBasicBlock);
    }

    public IrValue GetSrcValue() {
        return this.useValueList.get(0);
    }

    public IrValue GetDstValue() {
        return this.useValueList.get(1);
    }

    public void SetSrcValue(IrValue srcValue) {
        this.GetSrcValue().DeleteUser(this);
        this.useValueList.set(0, srcValue);
    }

    @Override
    public boolean DefValue() {
        return false;
    }

    @Override
    public String GetGvnHash() {
        return this.GetSrcValue().GetIrName();
    }

    @Override
    public String toString() {
        return "move " + this.GetDstValue().GetIrName() + ", " + this.GetSrcValue().GetIrName();
    }

    @Override
    public void toMips() {
        super.toMips();

        IrValue srcValue = this.GetSrcValue();
        IrValue dstValue = this.GetDstValue();
        Register srcRegister = MipsBuilder.GetValueToRegister(srcValue);
        Register dstRegister = MipsBuilder.GetValueToRegister(dstValue);

        // 不需要move
        if (srcRegister != null && srcRegister.equals(dstRegister)) {
            return;
        }
        dstRegister = this.GetRegisterOrK0ForValue(dstValue);
        this.LoadValueToRegister(srcValue, dstRegister);
        this.SaveRegisterResult(dstValue, dstRegister);
    }
}
