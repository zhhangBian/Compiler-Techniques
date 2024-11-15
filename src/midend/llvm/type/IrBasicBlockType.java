package midend.llvm.type;

public class IrBasicBlockType extends IrType {
    public static final IrBasicBlockType BASIC_BLOCK = new IrBasicBlockType();

    private IrBasicBlockType() {
    }

    @Override
    public String toString() {
        return "";
    }
}
