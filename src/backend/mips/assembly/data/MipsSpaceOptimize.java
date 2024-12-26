package backend.mips.assembly.data;

import midend.llvm.constant.IrConstant;
import midend.llvm.constant.IrConstantInt;
import midend.llvm.type.IrType;

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
        for (int count = this.valueList.size(); count < this.size; count++) {
            this.valueList.add(new IrConstantInt(0));
        }
    }

    private int GetNotZeroIndex() {
        int index = -1;
        for (int i = 0; i < this.valueList.size(); i++) {
            if (Integer.parseInt(valueList.get(i).GetIrName()) != 0) {
                index = i;
            }
        }
        return index;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.name + ":");

        int index = this.GetNotZeroIndex();

        IrType irType = this.valueList.get(0).GetIrType();
        // char
        if (irType.IsInt8Type()) {
            if (index >= 0) {
                builder.append("\t.byte ");
                StringJoiner joiner = new StringJoiner(", ");
                for (int i = 0; i <= this.GetNotZeroIndex(); i++) {
                    joiner.add(this.valueList.get(i).GetIrName());
                }

                builder.append(joiner);
                builder.append("\n\t");
                builder.append("\t.space ");
                // 对齐
                builder.append(this.size - (index + 1) + (4 - this.size % 4));
            } else {
                builder.append("\t.space ");
                // 对齐
                builder.append(this.size + 4 - this.size % 4);
            }
        }
        // int
        else {
            if (index >= 0) {
                builder.append("\t.word ");
                StringJoiner joiner = new StringJoiner(", ");
                for (int i = 0; i <= this.GetNotZeroIndex(); i++) {
                    joiner.add(this.valueList.get(i).GetIrName());
                }

                builder.append(joiner);
                builder.append("\n\t");
            }
            builder.append("\t.space ");
            builder.append(4 * (this.size - index - 1));
        }

        return builder.toString();
    }
}
