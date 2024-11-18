package midend.llvm.type;

public class IrFunctionType extends IrType {
    private final IrType returnType;

    public IrFunctionType(IrType returnType) {
        this.returnType = returnType;
    }

    public IrType GetReturnType() {
        return this.returnType;
    }

    @Override
    public String toString() {
        return this.returnType.toString();
    }
}
