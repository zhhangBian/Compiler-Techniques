package midend.llvm.value;

import midend.llvm.type.IrBaseType;

public class IrConst extends IrValue {
    private final int constValue;

    public IrConst(int constValue) {
        super(IrBaseType.INT32, String.valueOf(constValue));
        this.constValue = constValue;
    }

    public int GetConstValue() {
        return this.constValue;
    }
}
