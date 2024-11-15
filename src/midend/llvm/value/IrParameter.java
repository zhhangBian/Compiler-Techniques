package midend.llvm.value;

import midend.llvm.type.IrType;

public class IrParameter extends IrValue {
    public IrParameter(IrType irType, String irName) {
        super(irType, irName);
    }

    @Override
    public String toString() {
        return this.irType + " " + this.irName;
    }
}
