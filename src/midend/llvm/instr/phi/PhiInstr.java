package midend.llvm.instr.phi;

import midend.llvm.IrBuilder;
import midend.llvm.constant.IrConstantInt;
import midend.llvm.instr.Instr;
import midend.llvm.instr.InstrType;
import midend.llvm.type.IrType;
import midend.llvm.use.IrUse;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrValue;

import java.util.ArrayList;
import java.util.StringJoiner;

public class PhiInstr extends Instr {
    private final ArrayList<IrBasicBlock> beforeBlockList;

    public PhiInstr(IrType irType, IrBasicBlock irBasicBlock) {
        super(irType, InstrType.PHI,
            IrBuilder.GetLocalVarName(irBasicBlock.GetIrFunction()), false);
        this.SetInBasicBlock(irBasicBlock);

        this.beforeBlockList = irBasicBlock.GetBeforeBlocks();
        // 填充相应的value，等待后续替换
        for (int i = 0; i < this.beforeBlockList.size(); i++) {
            this.AddUseValue(null);
        }
    }

    public ArrayList<IrBasicBlock> GetBeforeBlockList() {
        return this.beforeBlockList;
    }

    public void ConvertBlockToValue(IrValue irValue, IrBasicBlock beforeBlock) {
        int index = this.beforeBlockList.indexOf(beforeBlock);
        // 进行相应的值替换
        this.useValueList.set(index, irValue);
        // 添加use关系
        irValue.AddUse(new IrUse(this, irValue));
    }

    @Override
    public boolean DefValue() {
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(this.irName);
        builder.append(" = phi ");
        builder.append(this.irType);

        StringJoiner joiner = new StringJoiner(", ");
        for (int i = 0; i < this.beforeBlockList.size(); i++) {
            final StringBuilder blockBuilder = new StringBuilder();
            blockBuilder.append("[ ");
            blockBuilder.append(this.useValueList.get(i) == null ?"null" :this.useValueList.get(i).GetIrName());
            blockBuilder.append(", %");
            blockBuilder.append(this.beforeBlockList.get(i).GetIrName());
            blockBuilder.append(" ]");
            joiner.add(blockBuilder);
        }
        builder.append(joiner);

        return builder.toString();
    }

    @Override
    public void toMips() {
        throw new RuntimeException("phi not delete!");
    }
}
