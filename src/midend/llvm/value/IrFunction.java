package midend.llvm.value;

import midend.llvm.IrBuilder;
import midend.llvm.constant.IrConstantChar;
import midend.llvm.constant.IrConstantInt;
import midend.llvm.instr.ReturnInstr;
import midend.llvm.type.IrFunctionType;
import midend.llvm.type.IrType;

import java.util.ArrayList;

public class IrFunction extends IrValue {
    private final IrType returnType;
    private final ArrayList<IrParameter> parameterList;
    private final ArrayList<IrBasicBlock> basicBlockList;

    public IrFunction(String name, IrType returnType) {
        super(IrFunctionType.FUNCTION_TYPE, name);
        this.returnType = returnType;

        this.parameterList = new ArrayList<>();
        this.basicBlockList = new ArrayList<>();
    }

    public IrType GetReturnType() {
        return this.returnType;
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

    public ArrayList<IrBasicBlock> GetBasicBlockList() {
        return this.basicBlockList;
    }

    // 对于void类型的函数，文法允许不含return，但是LLVM一定需要return
    public void CheckHaveReturn() {
        IrBasicBlock basicBlock = IrBuilder.GetCurrentBasicBlock();
        if (!basicBlock.LastInstrIsReturn()) {
            IrValue returnValue = null;
            if (this.returnType.IsInt8Type()) {
                returnValue = new IrConstantChar(0);
            } else if (this.returnType.IsInt32Type()) {
                returnValue = new IrConstantInt(0);
            }
            ReturnInstr returnInstr = new ReturnInstr(IrBuilder.GetFunctionVarName(), returnValue);
            IrBuilder.AddInstr(returnInstr);
        }

    }
}
