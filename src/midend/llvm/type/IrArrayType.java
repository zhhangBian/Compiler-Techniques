package midend.llvm.type;

public class IrArrayType extends IrType {
    private final int arraySize;
    private final IrBaseType elementType;

    public IrArrayType(int arraySize, IrBaseType elementType) {
        this.arraySize = arraySize;
        this.elementType = elementType;
    }

    public int GetArraySize() {
        return this.arraySize;
    }

    public IrBaseType GetElementType() {
        return this.elementType;
    }
}
