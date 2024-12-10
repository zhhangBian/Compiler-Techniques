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
    }

    public void RemoveAllValueUse() {
        // 去除使用的value
        for (IrValue useValue : this.useValueList) {
            useValue.DeleteUser(this);
        }
        this.useValueList.clear();
        // 对使用该value的使用者清空
        // 不知道为什么，但是有bug
        //for (IrUse irUse : this.useList) {
        //    irUse.GetUser().useValueList.remove(this);
        //}
    }
}
