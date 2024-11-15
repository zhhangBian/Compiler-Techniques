package midend.llvm.type;

public class IrFunctionType extends IrType {
    public static final IrFunctionType FUNCTION_TYPE = new IrFunctionType();

    private IrFunctionType() {
    }

    @Override
    public String toString() {
        return "";
    }
}
