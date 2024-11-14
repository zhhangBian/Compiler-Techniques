package midend.llvm.value;

import midend.llvm.IrBuilder;
import midend.llvm.constant.IrConstant;
import midend.llvm.type.IrType;
import midend.llvm.use.IrUser;

public class IrGlobalValue extends IrUser {
    private final IrConstant initValue;

    public IrGlobalValue(IrType valueType, String name, IrConstant initValue) {
        super(valueType, name);
        this.initValue = initValue;

    }
}
