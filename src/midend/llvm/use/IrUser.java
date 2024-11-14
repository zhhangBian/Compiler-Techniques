package midend.llvm.use;

import midend.llvm.type.IrType;
import midend.llvm.value.IrValue;

import java.util.ArrayList;

public class IrUser extends IrValue {
    // 使用的value list
    private final ArrayList<IrValue> useValueList;

    public IrUser(IrType valueType, String name) {
        super(valueType, name);
        this.useValueList = new ArrayList<>();
    }

    public void AddUseValue(IrValue value) {
        // 分别注册use和user关系
        this.useValueList.add(value);
        value.AddUse(new IrUse(this, value));
    }

    public ArrayList<IrValue> GetUseValueList() {
        return this.useValueList;
    }
}
