package midend.llvm.value;

import midend.llvm.type.IrType;
import midend.llvm.use.IrUse;
import midend.llvm.use.IrUser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

public class IrValue {
    protected final IrType irType;
    protected final String irName;
    // 使用当前value的use关系，即使用该value的user列表
    protected final ArrayList<IrUse> useList;

    public IrValue(IrType irType, String irName) {
        this.irType = irType;
        this.irName = irName;
        this.useList = new ArrayList<>();
    }

    public IrType GetIrType() {
        return this.irType;
    }

    public String GetIrName() {
        return this.irName;
    }

    public void AddUse(IrUse use) {
        this.useList.add(use);
    }

    public ArrayList<IrUse> GetUseList() {
        return this.useList;
    }

    public void ModifyUsersToNewValue(IrValue newValue) {
        // 对于user替换为新的value
        ArrayList<IrUser> userList = this.useList.stream().map(IrUse::GetUser).
            collect(Collectors.toCollection(ArrayList::new));
        for (IrUser user : userList) {
            user.ModifyValue(this, newValue);
        }
    }

    public void DeleteUser(IrUser user) {
        Iterator<IrUse> iterator = this.useList.iterator();
        while (iterator.hasNext()) {
            IrUse use = iterator.next();
            if (use.GetUser() == user) {
                iterator.remove();
                break;
            }
        }
    }

    public void toMips() {
    }

    public String GetMipsLabel() {
        return this.irName.substring(1);
    }
}
