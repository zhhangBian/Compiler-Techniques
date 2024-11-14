package midend.llvm.use;

import midend.llvm.value.IrValue;

// 描述了Use关系:user使用其他value
public class IrUse {
    private final IrUser user;
    private final IrValue value;

    public IrUse(IrUser user, IrValue value) {
        this.user = user;
        this.value = value;
    }

    public IrUser GetUser() {
        return this.user;
    }

    public IrValue GetValue() {
        return this.value;
    }
}
