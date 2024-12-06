package midend.llvm.use;

import midend.llvm.type.IrType;
import midend.llvm.value.IrValue;

import java.util.ArrayList;

public class IrUser extends IrValue {
    // 使用的value的list
    protected final ArrayList<IrValue> useValueList;

    public IrUser(IrType valueType, String name) {
        super(valueType, name);
        this.useValueList = new ArrayList<>();
    }

    public void AddUseValue(IrValue irValue) {
        this.useValueList.add(irValue);
        // 在添加的同时登记Use关系
        if (irValue != null) {
            irValue.AddUse(new IrUse(this, irValue));
        }
    }

    public ArrayList<IrValue> GetUseValueList() {
        return this.useValueList;
    }

    public void ModifyValue(IrValue oldValue, IrValue newValue) {
        // 将newValue加入到oldValue中，位置不变
        int index = this.useValueList.indexOf(oldValue);
        this.useValueList.set(index, newValue);

        // 将this从oldValue的useList中删除
        oldValue.DeleteUser(this);
        // 将this加入到newValue的useList中
        newValue.AddUse(new IrUse(this, newValue));
    }
}
