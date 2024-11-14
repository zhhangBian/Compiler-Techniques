package midend.llvm.constant;

import midend.llvm.type.IrBaseType;

public class IrConstantInt extends IrConstant {
    private final int value;

    public IrConstantInt(int value) {
        super(IrBaseType.INT32, String.valueOf(value));
        this.value = value;
    }

    public int GetValue() {
        return this.value;
    }
}
