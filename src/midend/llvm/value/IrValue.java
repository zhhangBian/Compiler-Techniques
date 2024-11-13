package midend.llvm.value;

import midend.llvm.type.IrType;

public class IrValue {
    protected final IrType irType;
    protected final String irName;

    public IrValue(IrType irType, String irName) {
        this.irType = irType;
        this.irName = irName;
    }
}
