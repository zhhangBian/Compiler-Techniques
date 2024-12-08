package midend.llvm.instr;

import backend.mips.MipsBuilder;
import backend.mips.Register;
import backend.mips.assembly.MipsLsu;
import midend.llvm.type.IrPointerType;
import midend.llvm.value.IrValue;

public class LoadInstr extends Instr {
    public LoadInstr(IrValue pointer) {
        super(((IrPointerType) pointer.GetIrType()).GetTargetType(), InstrType.LOAD);
        this.AddUseValue(pointer);
    }

    @Override
    public boolean DefValue() {
        return true;
    }

    @Override
    public String toString() {
        IrValue pointer = this.GetPointer();
        return this.irName + " = load " + this.irType + ", " +
            pointer.GetIrType() + " " + pointer.GetIrName();
    }

    @Override
    public void toMips() {
        super.toMips();

        IrValue pointer = this.GetPointer();
        // 加载address的寄存器
        Register pointerRegister = this.GetRegisterOrK0ForValue(pointer);
        // 加载指value的寄存器
        Register targetRegister = this.GetRegisterOrK0ForValue(this);

        // 加载指针的地址
        this.LoadValueToRegister(pointer, pointerRegister);
        // 加载数据
        new MipsLsu(MipsLsu.LsuType.LW, targetRegister, pointerRegister, 0);
        // 将数据保存到栈空间上
        if (MipsBuilder.GetValueToRegister(this) == null) {
            int offset = MipsBuilder.AllocateStackForValue(this);
            new MipsLsu(MipsLsu.LsuType.SW, targetRegister, Register.SP, offset);
        }
    }

    private IrValue GetPointer() {
        return this.useValueList.get(0);
    }
}
