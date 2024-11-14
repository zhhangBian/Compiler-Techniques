package midend.llvm.value;

import midend.llvm.type.IrType;

public class IrParameter extends IrValue {
    private final IrFunction irFunction;

    public IrParameter(IrType irType, String irName, IrFunction irFunction) {
        super(irType, irName);
        this.irFunction = irFunction;
    }
}
