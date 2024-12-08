package midend.llvm.constant;

import backend.mips.Register;
import backend.mips.assembly.MipsLsu;
import backend.mips.assembly.data.MipsSpace;
import backend.mips.assembly.data.MipsSpaceOptimize;
import backend.mips.assembly.fake.MarsLi;
import midend.llvm.type.IrArrayType;
import midend.llvm.type.IrType;
import utils.Setting;

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

    public int GetArraySize() {
        return this.arraySize;
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
        this.MipsDeclare(this.GetMipsLabel());
    }

    @Override
    public void MipsDeclare(String label) {
        if (Setting.FINE_TUNING) {
            new MipsSpaceOptimize(label, this.arraySize, this.valueList);
        } else {
            // 先申请空间
            // TODO：按照char类型细化大小
            new MipsSpace(label, this.arraySize * 4);
            // 进行值初始化
            int offset = 0;
            for (IrConstant irConstant : this.valueList) {
                if (irConstant instanceof IrConstantInt irConstantInt) {
                    new MarsLi(Register.T0, irConstantInt.GetValue());
                    new MipsLsu(MipsLsu.LsuType.SW, Register.T0, label, offset);
                } else if (irConstant instanceof IrConstantChar irConstantChar) {
                    new MarsLi(Register.T0, irConstantChar.GetValue());
                    new MipsLsu(MipsLsu.LsuType.SW, Register.T0, label, offset);
                }
                offset += 4;
            }
        }
    }
}
