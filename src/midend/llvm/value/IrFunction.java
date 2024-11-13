package midend.llvm.value;

import midend.llvm.type.IrFunctionType;
import midend.llvm.type.IrType;

import java.util.ArrayList;

public class IrFunction extends IrValue {
    private final IrType returnType;
    private final ArrayList<IrParameter> parameters;
    private final ArrayList<IrBasicBlock> basicBlocks;

    public IrFunction(String name, IrType returnType) {
        super(IrFunctionType.FUNCTION_TYPE, name);
        this.returnType = returnType;

        this.parameters = new ArrayList<>();
        this.basicBlocks = new ArrayList<>();
    }

    public IrType GetReturnType() {
        return this.returnType;
    }

    public void AddParameter(IrParameter parameter) {
        this.parameters.add(parameter);
    }

    public void AddBasicBlock(IrBasicBlock basicBlock) {
        this.basicBlocks.add(basicBlock);
    }
}
