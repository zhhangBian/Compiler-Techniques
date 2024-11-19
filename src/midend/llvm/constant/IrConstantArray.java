package midend.llvm.constant;

import midend.llvm.type.IrArrayType;
import midend.llvm.type.IrType;

import java.util.ArrayList;

public class IrConstantArray extends IrConstant {
    private final ArrayList<IrConstant> valueList;
    private final int arraySize;

    public IrConstantArray(int arraySize, IrType elementType, String irName,
                           ArrayList<IrConstant> initValues) {
        super(new IrArrayType(arraySize, elementType), irName);
        this.valueList = initValues == null ? new ArrayList<>() : new ArrayList<>(initValues);
        this.arraySize = arraySize;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.irType);
        builder.append(" ");

        if (this.valueList.isEmpty()) {
            builder.append("zeroinitializer");
        } else {
            builder.append("[");
            for (int i = 0; i < this.valueList.size() - 1; i++) {
                builder.append(this.valueList.get(i));
                builder.append(", ");
            }
            builder.append(this.valueList.get(this.valueList.size() - 1));

            String padding = ", " + this.valueList.get(0).GetIrType() + " 0";
            builder.append(padding.repeat(Math.max(0, this.arraySize - this.valueList.size())));
            builder.append("]");
        }

        return builder.toString();
    }

    @Override
    public void toMips() {

    }
}
