package midend.llvm.constant;

import midend.llvm.type.IrArrayType;
import midend.llvm.type.IrType;

import java.util.ArrayList;

public class IrConstantArray extends IrConstant {
    private final ArrayList<Integer> valueList;

    public IrConstantArray(int size, IrType elementTyper, String irName) {
        super(new IrArrayType(size, elementTyper), irName);
        this.valueList = new ArrayList<>();
    }

    public IrConstantArray(int size, IrType elementType, String irName,
                           ArrayList<Integer> initValues) {
        super(new IrArrayType(size, elementType), irName);
        this.valueList = new ArrayList<>(initValues);
    }

    public ArrayList<Integer> GetValueList() {
        return this.valueList;
    }
}
