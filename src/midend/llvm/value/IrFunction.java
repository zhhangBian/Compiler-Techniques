package midend.llvm.value;

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
}
