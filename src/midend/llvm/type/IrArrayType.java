package midend.llvm.type;

public class IrArrayType extends IrType {
    private final int arraySize;
    private final IrType elementType;

    public IrArrayType(int arraySize, IrType elementType) {
        this.arraySize = arraySize;
        this.elementType = elementType;
    }

    public int GetArraySize() {
        return this.arraySize;
    }

    public IrType GetElementType() {
        return this.elementType;
    }
}
