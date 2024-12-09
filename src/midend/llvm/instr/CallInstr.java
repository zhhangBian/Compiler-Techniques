package midend.llvm.instr;

import backend.mips.MipsBuilder;
import backend.mips.Register;
import backend.mips.assembly.MipsAlu;
import backend.mips.assembly.MipsJump;
import backend.mips.assembly.MipsLsu;
import midend.llvm.IrBuilder;
import midend.llvm.value.IrFunction;
import midend.llvm.value.IrParameter;
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
    public boolean DefValue() {
        return !this.IsVoidReturnType();
    }

    @Override
    public String toString() {
        final IrFunction targetFunction = this.GetTargetFunction();
        ArrayList<String> paramInfo = new ArrayList<>();
        for (IrValue param : this.GetParamList()) {
            paramInfo.add(param.GetIrType() + " " + param.GetIrName());
        }

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

        ArrayList<IrValue> paramList = this.GetParamList();
        // 将参数填入对应位置
        this.FillParams(paramList, currentOffset, allocatedRegisterList);
        currentOffset = currentOffset - 4 * allocatedRegisterList.size() - 8;

        // 设置新的栈地址
        new MipsAlu(MipsAlu.AluType.ADDI, Register.SP, Register.SP, currentOffset);
        // 跳转到函数
        IrFunction targetFunction = this.GetTargetFunction();
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

    private void FillParams(ArrayList<IrValue> paramList, int currentOffset,
                            ArrayList<Register> allocatedRegisterList) {
        for (int i = 0; i < paramList.size(); i++) {
            IrValue param = paramList.get(i);
            // 需要填入相应的寄存器中
            if (i < 3) {
                Register paramRegister = Register.get(Register.A0.ordinal() + i + 1);
                // 如果是参数：由于赋值冲突，从栈中取值，之前保护现场时已存入内存
                if (param instanceof IrParameter) {
                    Register paraRegister = MipsBuilder.GetValueToRegister(param);
                    if (allocatedRegisterList.contains(paraRegister)) {
                        new MipsLsu(MipsLsu.LsuType.LW, paramRegister, Register.SP,
                            currentOffset - 4 * allocatedRegisterList.indexOf(paraRegister) - 4);
                    } else {
                        this.LoadValueToRegister(param, paramRegister);
                    }
                } else {
                    this.LoadValueToRegister(param, paramRegister);
                }
            }
            // 直接压入栈中
            else {
                Register tempRegister = Register.K0;
                // 如果是参数：由于赋值冲突，从栈中取值
                if (param instanceof IrParameter) {
                    Register paraRegister = MipsBuilder.GetValueToRegister(param);
                    if (allocatedRegisterList.contains(paraRegister)) {
                        new MipsLsu(MipsLsu.LsuType.LW, tempRegister, Register.SP,
                            currentOffset - 4 * allocatedRegisterList.indexOf(paraRegister) - 4);
                    } else {
                        this.LoadValueToRegister(param, tempRegister);
                    }
                } else {
                    this.LoadValueToRegister(param, tempRegister);
                }
                new MipsLsu(MipsLsu.LsuType.SW, tempRegister, Register.SP,
                    currentOffset - 4 * allocatedRegisterList.size() - 8 - 4 * i - 4);
            }
        }
    }

    private void RecoverCurrent(int formerOffset, ArrayList<Register> allocatedRegisterList) {
        // 恢复RA寄存器和SP寄存器
        new MipsLsu(MipsLsu.LsuType.LW, Register.RA, Register.SP, 0);
        new MipsLsu(MipsLsu.LsuType.LW, Register.SP, Register.SP, 4);

        // 恢复原先的寄存器
        // 此时sp已经恢复了
        int registerNum = 0;
        for (Register register : allocatedRegisterList) {
            registerNum++;
            new MipsLsu(MipsLsu.LsuType.LW, register, Register.SP,
                formerOffset - registerNum * 4);
        }
    }

    private void HandleReturnValue() {
        this.SaveRegisterResult(this, Register.V0);
    }
}
