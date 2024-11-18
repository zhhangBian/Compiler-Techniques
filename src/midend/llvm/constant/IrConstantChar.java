package midend.llvm.constant;

import midend.llvm.type.IrBaseType;

public class IrConstantChar extends IrConstant {
    private final char value;

    public IrConstantChar(int value) {
        super(IrBaseType.INT8, String.valueOf(value));
        this.value = (char) value;
    }

    public int GetValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "i8 " + (int)(this.value);
    }
}
