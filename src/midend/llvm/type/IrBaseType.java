package midend.llvm.type;

public class IrBaseType extends IrType {
    public static final IrBaseType VOID = new IrBaseType(0);
    public static final IrBaseType INT1 = new IrBaseType(1);
    public static final IrBaseType INT8 = new IrBaseType(8);
    public static final IrBaseType INT32 = new IrBaseType(32);

    private final int bitWidth;

    private IrBaseType(int bitWidth) {
        this.bitWidth = bitWidth;
    }

    @Override
    public String toString() {
        return switch (this.bitWidth) {
            case 0 -> "void";
            case 1 -> "i1";
            case 8 -> "i8";
            case 32 -> "i32";
            default -> throw new RuntimeException("Error IrBaseType");
        };
    }

    public static IrBaseType GetIrBaseType(int bitWidth) {
        return switch (bitWidth) {
            case 0 -> IrBaseType.VOID;
            case 1 -> IrBaseType.INT1;
            case 8 -> IrBaseType.INT8;
            case 32 -> IrBaseType.INT32;
            default -> throw new RuntimeException("Error IrBaseType");
        };
    }
}
