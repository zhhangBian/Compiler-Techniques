package midend.llvm.constant;

import midend.llvm.type.IrType;
import midend.llvm.value.IrValue;

public abstract class IrConstant extends IrValue {
    public IrConstant(IrType irType, String irName) {
        super(irType, irName);
    }
}
