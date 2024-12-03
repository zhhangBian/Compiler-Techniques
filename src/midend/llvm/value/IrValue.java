package midend.llvm.value;

import midend.llvm.type.IrType;
import midend.llvm.use.IrUse;

import java.util.ArrayList;

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

    public void toMips() {
    }

    public String GetMipsLabel() {
        return this.irName.substring(1);
    }
}
