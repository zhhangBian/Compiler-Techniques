package midend.llvm.value;

import midend.llvm.type.IrType;

public class IrParameter extends IrValue {

    public IrParameter(IrType irType, String irName) {
        super(irType, irName);
    }
}
