package midend.llvm.instr;

import backend.mips.MipsBuilder;
import backend.mips.Register;
import backend.mips.assembly.fake.MarsLi;
import backend.mips.assembly.fake.MarsMove;
import midend.llvm.constant.IrConstant;
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
        this.useValueList.set(0, srcValue);
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
        // 常值
        if (srcValue instanceof IrConstant) {
            new MarsLi(dstRegister, Integer.parseInt(srcValue.GetIrName()));
        }
        // 有寄存器
        else if (srcRegister != null) {
            new MarsMove(dstRegister, srcRegister);
        }
        // 无寄存器
        else {
            this.LoadValueToRegister(srcValue, dstRegister);
        }
        this.SaveResult(dstValue, dstRegister);
    }
}
