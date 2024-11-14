package midend.llvm.constant;

import midend.llvm.type.IrBaseType;

public class IrConstantChar extends IrConstant {
    private final int value;

    public IrConstantChar(int value) {
        super(IrBaseType.INT8, String.valueOf(value));
        this.value = value;
    }

    public int GetValue() {
        return this.value;
    }
}
