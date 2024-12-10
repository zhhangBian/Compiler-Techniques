package midend.llvm.instr;

import backend.mips.MipsBuilder;
import backend.mips.Register;
import backend.mips.assembly.MipsAnnotation;
import backend.mips.assembly.MipsLsu;
import backend.mips.assembly.fake.MarsLa;
import backend.mips.assembly.fake.MarsLi;
import backend.mips.assembly.fake.MarsMove;
import midend.llvm.IrBuilder;
import midend.llvm.constant.IrConstant;
import midend.llvm.type.IrType;
import midend.llvm.use.IrUser;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrGlobalValue;
import midend.llvm.value.IrValue;

// instr是一种User：使用其他的Value作为参数
public abstract class Instr extends IrUser {
    private final InstrType instrType;
    private IrBasicBlock inBasicBlock;

    public Instr(IrType irType, InstrType instrType) {
        super(irType, IrBuilder.GetLocalVarName());
        this.instrType = instrType;
        // 自动插入
        IrBuilder.AddInstr(this);
    }

    public Instr(IrType irType, InstrType instrType, String irName) {
        super(irType, irName);
        this.instrType = instrType;
        // 自动插入
        IrBuilder.AddInstr(this);
    }

    public Instr(IrType irType, InstrType instrType, String irName, boolean autoAdd) {
        super(irType, irName);
        this.instrType = instrType;
        // 自动插入
        if (autoAdd) {
            IrBuilder.AddInstr(this);
        }
    }

    public void SetInBasicBlock(IrBasicBlock irBasicBlock) {
        this.inBasicBlock = irBasicBlock;
    }

    public InstrType GetInstrType() {
        return this.instrType;
    }

    public IrBasicBlock GetInBasicBlock() {
        return this.inBasicBlock;
    }

    public abstract boolean DefValue();

    public boolean IsBlockOutValue(IrValue useValue) {
        return this.inBasicBlock.GetOutValueSet().contains(useValue);
    }

    public String GetGvnHash() {
        return null;
    }

    @Override
    public abstract String toString();

    @Override
    public void toMips() {
        new MipsAnnotation(this.toString());
    }

    // 将在内存中的值加载到寄存器
    protected void LoadValueToRegister(IrValue irValue, Register targetRegister) {
        // 如果是常量
        if (irValue instanceof IrConstant irConstant) {
            new MarsLi(targetRegister, Integer.parseInt(irConstant.GetIrName()));
            return;
        }
        // 如果是指针形变量
        if (irValue instanceof IrGlobalValue irGlobalValue) {
            new MarsLa(targetRegister, irGlobalValue.GetMipsLabel());
            return;
        }
        // 如果有已经分配的寄存器
        Register valueRegister = MipsBuilder.GetValueToRegister(irValue);
        if (valueRegister != null) {
            new MarsMove(targetRegister, valueRegister);
            return;
        }

        Integer stackValueOffset = MipsBuilder.GetStackValueOffset(irValue);
        // 若不在内存中，则分配一块
        if (stackValueOffset == null) {
            stackValueOffset = MipsBuilder.AllocateStackForValue(irValue);
        }
        new MipsLsu(MipsLsu.LsuType.LW, targetRegister, Register.SP, stackValueOffset);
    }

    // 得到分配的寄存器，若没有则使用K0
    protected Register GetRegisterOrK0ForValue(IrValue irValue) {
        Register register = MipsBuilder.GetValueToRegister(irValue);
        return register == null ? Register.K0 : register;
    }

    // 得到分配的寄存器，若没有则使用K1
    protected Register GetRegisterOrK1ForValue(IrValue irValue) {
        Register register = MipsBuilder.GetValueToRegister(irValue);
        return register == null ? Register.K1 : register;
    }

    // 保存寄存器中的计算结果，若没分配寄存器则保留到栈上
    protected void SaveRegisterResult(IrValue irValue, Register valueRegister) {
        Register register = MipsBuilder.GetValueToRegister(irValue);
        if (register == null) {
            int offset = MipsBuilder.AllocateStackForValue(irValue);
            new MipsLsu(MipsLsu.LsuType.SW, valueRegister, Register.SP, offset);
        } else {
            new MarsMove(register, valueRegister);
        }
    }
}
