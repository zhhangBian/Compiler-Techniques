package midend.llvm.type;

public class IrPointerType extends IrType {
    private final IrType targetType;

    public IrPointerType(IrType targetType) {
        this.targetType = targetType;
    }

    public IrType GetTargetType() {
        return this.targetType;
    }

    @Override
    public String toString() {
        return this.targetType + "*";
    }
}
