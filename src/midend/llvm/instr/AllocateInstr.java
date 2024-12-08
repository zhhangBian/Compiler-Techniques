package midend.llvm.instr;

import backend.mips.MipsBuilder;
import backend.mips.Register;
import backend.mips.assembly.MipsAlu;
import backend.mips.assembly.MipsLsu;
import midend.llvm.type.IrArrayType;
import midend.llvm.type.IrPointerType;
import midend.llvm.type.IrType;

public class AllocateInstr extends Instr {
    private final IrType targetType;

    public AllocateInstr(IrType targetType) {
        super(new IrPointerType(targetType), InstrType.ALLOCATE);
        this.targetType = targetType;
    }

    public IrType GetTargetType() {
        return this.targetType;
    }

    @Override
    public boolean DefValue() {
        return true;
    }

    @Override
    public String toString() {
        return this.irName + " = alloca " + this.targetType;
    }

    @Override
    public void toMips() {
        super.toMips();
        // 起到两个作用：
        // 1. 在栈上开辟空间，存放局部变量
        // 2. 得到指向对应地址的指针

        // 为数据在栈上开辟空间
        // 局部数组全部分配到栈上
        if (this.targetType instanceof IrArrayType irArrayType) {
            MipsBuilder.AllocateStackSpace(4 * irArrayType.GetArraySize());
        } else {
            MipsBuilder.AllocateStackSpace(4);
        }

        // 紧随其后创建指针
        int pointerOffset = MipsBuilder.GetCurrentStackOffset();
        Register register = MipsBuilder.GetValueToRegister(this);
        if (register != null) {
            new MipsAlu(MipsAlu.AluType.ADDI, register, Register.SP, pointerOffset);
        } else {
            new MipsAlu(MipsAlu.AluType.ADDI, Register.K0, Register.SP, pointerOffset);
            pointerOffset = MipsBuilder.AllocateStackForValue(this);
            new MipsLsu(MipsLsu.LsuType.SW, Register.K0, Register.SP, pointerOffset);
        }
    }
}
