package midend.llvm.constant;

import midend.llvm.type.IrType;

public class IrConstantString extends IrConstant {
    private final String value;

    public IrConstantString(IrType irType, String value) {
        super(irType, value);
        this.value = value;
    }

    public String GetValue() {
        return this.value;
    }
}
