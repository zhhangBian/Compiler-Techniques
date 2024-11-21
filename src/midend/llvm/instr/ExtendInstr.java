package midend.llvm.instr;

import backend.mips.MipsBuilder;
import backend.mips.Register;
import backend.mips.assembly.MipsLsu;
import midend.llvm.type.IrType;
import midend.llvm.value.IrValue;

public class ExtendInstr extends Instr {
    private final IrType targetType;

    public ExtendInstr(IrValue originValue, IrType targetType) {
        super(targetType, InstrType.EXTEND);
        this.targetType = targetType;
        this.AddUseValue(originValue);
        //throw new RuntimeException("debug");
    }

    @Override
    public String toString() {
        IrValue originValue = this.GetOriginValue();
        return this.irName + " = zext " + originValue.GetIrType() + " " +
            originValue.GetIrName() + " to " + this.targetType;
    }

    @Override
    public void toMips() {
        // mips不需要位宽扩展，只需要将值进行映射，使得使用this的指令能使用到origin
        IrValue originValue = this.GetOriginValue();
        Register register = MipsBuilder.GetValueToRegister(originValue);
        // 如果已经分配了寄存器
        if (register != null) {
            // 为this开辟一个空间，将寄存器的值存储
            new MipsLsu(MipsLsu.LsuType.SW, register, Register.SP,
                MipsBuilder.AllocateStackForValue(this));
        }
        // 如果没有
        else {
            // 也添加值映射，使得访问相同的内存地址
            MipsBuilder.AddValueStackMapping(this, originValue);
        }
    }

    private IrValue GetOriginValue() {
        return this.useValueList.get(0);
    }
}
