package midend.llvm.type;

public class IrArrayType extends IrType {
    private final int arraySize;
    private final IrType elementType;

    public IrArrayType(int arraySize, IrType elementType) {
        this.arraySize = arraySize;
        this.elementType = elementType;
    }

    public IrType GetElementType() {
        return this.elementType;
    }

    public int GetArraySize() {
        return this.arraySize;
    }

    @Override
    public String toString() {
        return "[" + this.arraySize + " x " + this.elementType + "]";
    }
}
