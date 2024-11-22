package midend.llvm.instr;

import backend.mips.MipsBuilder;
import backend.mips.Register;
import backend.mips.assembly.MipsAlu;
import backend.mips.assembly.MipsJump;
import backend.mips.assembly.MipsLsu;
import backend.mips.assembly.fake.MarsMove;
import midend.llvm.IrBuilder;
import midend.llvm.value.IrFunction;
import midend.llvm.value.IrValue;

import java.util.ArrayList;

public class CallInstr extends Instr {
    public CallInstr(IrFunction targetFunction, ArrayList<IrValue> paramList) {
        super(targetFunction.GetReturnType(), InstrType.CALL,
            targetFunction.GetReturnType().IsVoidType() ? "call" : IrBuilder.GetLocalVarName());
        this.AddUseValue(targetFunction);
        paramList.forEach(this::AddUseValue);
    }

    public IrFunction GetTargetFunction() {
        return (IrFunction) this.useValueList.get(0);
    }

    public ArrayList<IrValue> GetParamList() {
        return new ArrayList<>(this.useValueList.subList(1, this.useValueList.size()));
    }

    private boolean IsVoidReturnType() {
        return this.GetTargetFunction().GetReturnType().IsVoidType();
    }

    @Override
    public String toString() {
        final IrFunction targetFunction = this.GetTargetFunction();
        ArrayList<String> paramInfo = new ArrayList<>();
        this.GetParamList().stream()
            .map(param -> param.GetIrType() + " " + param.GetIrName())
            .forEach(paramInfo::add);

        StringBuilder builder = new StringBuilder();
        // 如果不是void
        if (!this.IsVoidReturnType()) {
            builder.append(this.irName + " = ");
        }

        builder.append("call ");
        builder.append(this.IsVoidReturnType() ? "void" : targetFunction.GetReturnType());
        builder.append(" ");
        builder.append(targetFunction.GetIrName());
        // 填充参数
        builder.append("(");
        builder.append(String.join(", ", paramInfo));
        builder.append(")");

        return builder.toString();
    }

    @Override
    public void toMips() {
        super.toMips();

        // 现场信息
        int currentOffset = MipsBuilder.GetCurrentStackOffset();
        ArrayList<Register> allocatedRegisterList = MipsBuilder.GetAllocatedRegList();

        // 保护现场
        this.SaveCurrent(currentOffset, allocatedRegisterList);
        currentOffset = currentOffset - 4 * allocatedRegisterList.size() - 8;

        IrFunction targetFunction = this.GetTargetFunction();
        ArrayList<IrValue> paramList = this.GetParamList();

        // 将参数填入对应位置
        this.FillParams(paramList, currentOffset);

        // 设置新的栈地址
        new MipsAlu(MipsAlu.AluType.ADDI, Register.SP, Register.SP, currentOffset);
        // 跳转到函数
        new MipsJump(MipsJump.JumpType.JAL, targetFunction.GetMipsLabel());

        // 恢复现场
        currentOffset = currentOffset + 4 * allocatedRegisterList.size() + 8;
        this.RecoverCurrent(currentOffset, allocatedRegisterList);

        // 处理返回值
        this.HandleReturnValue();
    }

    private void SaveCurrent(int currentOffset, ArrayList<Register> allocatedRegisterList) {
        // 获取已分配的寄存器列表
        int registerNum = 0;
        for (Register register : allocatedRegisterList) {
            registerNum++;
            new MipsLsu(MipsLsu.LsuType.SW, register, Register.SP,
                currentOffset - registerNum * 4);
        }
        // 保存SP寄存器和RA寄存器
        new MipsLsu(MipsLsu.LsuType.SW, Register.SP, Register.SP,
            currentOffset - registerNum * 4 - 4);
        new MipsLsu(MipsLsu.LsuType.SW, Register.RA, Register.SP,
            currentOffset - registerNum * 4 - 8);
    }

    private void RecoverCurrent(int formerOffset, ArrayList<Register> allocatedRegisterList) {
        // 恢复RA寄存器和SP寄存器
        new MipsLsu(MipsLsu.LsuType.LW, Register.RA, Register.SP, 0);
        new MipsLsu(MipsLsu.LsuType.LW, Register.SP, Register.SP, 4);

        // 恢复原先的寄存器
        // 此时sp已经恢复了
        int registerNum = 0;
        for (Register register : allocatedRegisterList) {
            new MipsLsu(MipsLsu.LsuType.LW, register, Register.SP,
                formerOffset - registerNum * 4);
            registerNum++;
        }
    }

    private void FillParams(ArrayList<IrValue> paramList, int currentOffset) {
        for (int i = 0; i < paramList.size(); i++) {
            IrValue param = paramList.get(i);
            // 需要填入相应的寄存器中
            if (i < 3) {
                Register paramRegister = Register.get(Register.A0.ordinal() + i + 1);
                this.LoadValueToRegister(param, paramRegister);
            }
            // 直接压入栈中
            else {
                Register tempRegister = Register.K0;
                this.LoadValueToRegister(param, tempRegister);
                new MipsLsu(MipsLsu.LsuType.SW, tempRegister, Register.SP,
                    currentOffset - 4 * i - 4);
            }
        }
    }

    private void HandleReturnValue() {
        Register returnRegister = MipsBuilder.GetValueToRegister(this);
        if (returnRegister != null) {
            new MarsMove(returnRegister, Register.V0);
        } else {
            MipsBuilder.AllocateStackForValue(this);
            new MipsLsu(MipsLsu.LsuType.SW, Register.V0, Register.SP,
                MipsBuilder.GetCurrentStackOffset());
        }
    }
}
