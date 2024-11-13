package midend.llvm.value;

import midend.llvm.type.IrType;

import java.util.ArrayList;

public class IrUser extends IrValue {
    protected ArrayList<IrValue> operands;

    public IrUser(IrType irType, String name) {
        super(irType, name);
    }
}
