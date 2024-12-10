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

    // 将所有使用此value的user替换为使用新value
    public void ModifyAllUsersToNewValue(IrValue newValue) {
        ArrayList<IrUser> userList = this.useList.stream().map(IrUse::GetUser).
            collect(Collectors.toCollection(ArrayList::new));
        for (IrUser user : userList) {
            user.ModifyValue(this, newValue);
            this.DeleteUser(user);
            // 将this加入到newValue的useList中
            newValue.AddUse(new IrUse(user, newValue));
        }
    }

    public void DeleteUser(IrUser user) {
        Iterator<IrUse> iterator = this.useList.iterator();
        while (iterator.hasNext()) {
            IrUse use = iterator.next();
            if (use.GetUser() == user) {
                iterator.remove();
                return;
            }
        }
    }

    public void toMips() {
    }

    public String GetMipsLabel() {
        return this.irName.substring(1);
    }
}
