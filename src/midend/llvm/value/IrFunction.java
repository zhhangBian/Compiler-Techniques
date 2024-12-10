package midend.llvm.value;

import backend.mips.MipsBuilder;
import backend.mips.Register;
import backend.mips.assembly.MipsLabel;
import midend.llvm.IrBuilder;
import midend.llvm.constant.IrConstantChar;
import midend.llvm.constant.IrConstantInt;
import midend.llvm.instr.JumpInstr;
import midend.llvm.instr.ReturnInstr;
import midend.llvm.type.IrFunctionType;
import midend.llvm.type.IrType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class IrFunction extends IrValue {
    private final ArrayList<IrParameter> parameterList;
    private final ArrayList<IrBasicBlock> basicBlockList;
    private final HashMap<IrValue, Register> valueRegisterMap;

    public IrFunction(String name, IrType returnType) {
        super(new IrFunctionType(returnType), name);
        this.parameterList = new ArrayList<>();
        this.basicBlockList = new ArrayList<>();
        this.valueRegisterMap = new HashMap<>();
    }

    public boolean IsMainFunction() {
        return this.irName.equals("@main");
    }

    public IrType GetReturnType() {
        return ((IrFunctionType) (this.irType)).GetReturnType();
    }

    public void AddParameter(IrParameter parameter) {
        this.parameterList.add(parameter);
    }

    public ArrayList<IrParameter> GetParameterList() {
        return this.parameterList;
    }

    public void AddBasicBlock(IrBasicBlock basicBlock) {
        this.basicBlockList.add(basicBlock);
    }

    public void AddBasicBlock(IrBasicBlock basicBlock, IrBasicBlock afterBlock) {
        int index = this.basicBlockList.indexOf(afterBlock);
        this.basicBlockList.add(index, basicBlock);
    }

    public ArrayList<IrBasicBlock> GetBasicBlocks() {
        return this.basicBlockList;
    }

    // 对于void类型的函数，文法允许不含return，但是LLVM一定需要return
    public void CheckHaveReturn() {
        IrBasicBlock basicBlock = IrBuilder.GetCurrentBasicBlock();
        if (!basicBlock.LastInstrIsReturn()) {
            IrValue returnValue = null;
            if (this.GetReturnType().IsInt8Type()) {
                returnValue = new IrConstantChar(0);
            } else if (this.GetReturnType().IsInt32Type()) {
                returnValue = new IrConstantInt(0);
            }
            returnValue = returnValue == null ? null :
                IrType.ConvertType(returnValue, this.GetReturnType());
            ReturnInstr returnInstr = new ReturnInstr(returnValue);
        }
    }

    public void CheckNoEmptyBlock() {
        for (int i = 0; i < this.basicBlockList.size(); i++) {
            IrBasicBlock block = this.basicBlockList.get(i);
            if (block.IsEmptyBlock()) {
                block.AddInstr(new JumpInstr(this.basicBlockList.get(i + 1), block));
            }
        }
    }

    public HashMap<IrValue, Register> GetValueRegisterMap() {
        return this.valueRegisterMap;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        // 函数声明
        builder.append("define dso_local " + this.GetReturnType() + " " + this.irName);
        // 参数声明
        builder.append("(");
        builder.append(this.parameterList.stream().map(IrParameter::toString).
            collect(Collectors.joining(", ")));
        builder.append(") {\n");
        // 语句声明
        builder.append(this.basicBlockList.stream().map(IrBasicBlock::toString).
            collect(Collectors.joining("\n")));
        builder.append("\n}");
        return builder.toString();
    }

    @Override
    public void toMips() {
        new MipsLabel(this.GetMipsLabel());
        MipsBuilder.SetCurrentFunction(this);

        for (int i = 0; i < this.parameterList.size(); i++) {
            // 为前三个参数分配寄存器
            if (i < 3) {
                MipsBuilder.AllocateRegForParam(this.parameterList.get(i),
                    Register.get(Register.A0.ordinal() + i + 1));
            }
            // 在运行栈上分配空间
            MipsBuilder.AllocateStackForValue(this.parameterList.get(i));
        }

        for (IrBasicBlock irBasicBlock : this.basicBlockList) {
            irBasicBlock.toMips();
        }
    }
}
