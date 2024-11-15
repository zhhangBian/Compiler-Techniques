package midend.llvm.constant;

import midend.llvm.type.IrArrayType;
import midend.llvm.type.IrBaseType;
import midend.llvm.type.IrPointerType;

import java.util.ArrayList;

public class IrConstantString extends IrConstant {
    private final String stringValue;

    public IrConstantString(String name, String stringValue) {
        super(new IrPointerType(
            new IrArrayType(stringValue.length() + 1, IrBaseType.INT8)), name);
        this.stringValue = stringValue;
    }

    public String GetStringValue() {
        return this.stringValue;
    }

    @Override
    public String toString() {
        return this.irName + " = constant " +
            ((IrPointerType) this.irType).GetTargetType() +
            " c\"" + this.stringValue + "\\00\"";
    }

    public static String ConvertArrayToString(ArrayList<Integer> rawList) {
        StringBuilder builder = new StringBuilder();
        for (Integer num : rawList) {
            builder.append((char) num.intValue());
        }
        return builder.toString();
    }
}
