package midend.llvm.use;

import midend.llvm.type.IrType;
import midend.llvm.value.IrValue;

import java.util.ArrayList;

public class IrUser extends IrValue {
    // 使用的value list
    protected final ArrayList<IrValue> useValueList;

    public IrUser(IrType valueType, String name) {
        super(valueType, name);
        this.useValueList = new ArrayList<>();
    }

    public void AddUseValue(IrValue value) {
        this.useValueList.add(value);
        // 在添加的同时登记Use关系
        value.AddUse(new IrUse(this, value));
    }

    public ArrayList<IrValue> GetUseValueList() {
        return this.useValueList;
    }
}
