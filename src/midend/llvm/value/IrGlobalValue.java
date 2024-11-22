package midend.llvm.value;

import midend.llvm.constant.IrConstant;
import midend.llvm.type.IrType;
import midend.llvm.use.IrUser;

public class IrGlobalValue extends IrUser {
    private final IrConstant globalValue;

    public IrGlobalValue(IrType valueType, String name, IrConstant globalValue) {
        super(valueType, name);
        this.globalValue = globalValue;
    }

    @Override
    public String toString() {
        return this.irName + " = dso_local global " + this.globalValue;
    }

    @Override
    public void toMips() {
        globalValue.MipsDeclare(this.GetMipsLabel());
    }
}
