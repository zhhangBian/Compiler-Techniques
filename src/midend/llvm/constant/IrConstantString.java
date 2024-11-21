package midend.llvm.constant;

import backend.mips.assembly.data.MipsAsciiz;
import midend.llvm.type.IrArrayType;
import midend.llvm.type.IrBaseType;
import midend.llvm.type.IrPointerType;

import java.util.ArrayList;

public class IrConstantString extends IrConstant {
    private final String stringValue;

    public IrConstantString(String name, String stringValue) {
        super(new IrPointerType(
            new IrArrayType(GetStringLength(stringValue), IrBaseType.INT8)), name);
        this.stringValue = stringValue;
    }

    public String GetStringValue() {
        return this.stringValue;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.irName);
        builder.append(" = constant ");
        builder.append(((IrPointerType) this.irType).GetTargetType());
        // 拼接字符串
        builder.append(" c\"");
        builder.append(this.stringValue.replaceAll("\\\\n", "\\\\0A"));
        builder.append("\\00\"");
        return builder.toString();
    }

    @Override
    public void toMips() {
        // 创建在 asciiz
        new MipsAsciiz(this.GetMipsLabel(), this.stringValue);
    }

    public static String ConvertArrayToString(ArrayList<Integer> rawList) {
        StringBuilder builder = new StringBuilder();
        for (Integer num : rawList) {
            builder.append((char) num.intValue());
        }
        return builder.toString();
    }

    private static int GetStringLength(String string) {
        int length = 0;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) != '\\') {
                length++;
            } else {
                if (i != string.length() - 1 && string.charAt(i + 1) == 'n') {
                    i++;
                    length++;
                }
            }
        }
        return length + 1;
    }
}
