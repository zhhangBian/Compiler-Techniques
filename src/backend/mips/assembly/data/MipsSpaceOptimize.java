package backend.mips.assembly.data;

import midend.llvm.constant.IrConstant;

import java.util.ArrayList;
import java.util.StringJoiner;

public class MipsSpaceOptimize extends MipsDataAssembly {
    private final String name;
    private final int size;
    private final ArrayList<IrConstant> valueList;

    public MipsSpaceOptimize(String name, int size, ArrayList<IrConstant> valueList) {
        this.name = name;
        this.size = size;
        this.valueList = valueList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.name);
        builder.append(": .word ");

        StringJoiner joiner = new StringJoiner(", ");
        int count;
        // 填入初值
        for (count = 0; count < this.valueList.size(); count++) {
            joiner.add(this.valueList.get(count).GetIrName());
        }
        // 剩余补0
        for (; count < this.size; count++) {
            joiner.add("0");
        }
        builder.append(joiner);

        return builder.toString();
    }
}
