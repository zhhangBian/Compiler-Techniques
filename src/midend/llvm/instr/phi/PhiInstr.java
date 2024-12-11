package midend.llvm.instr.phi;

import midend.llvm.IrBuilder;
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

        this.beforeBlockList = new ArrayList<>(irBasicBlock.GetBeforeBlocks());
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
        // 进行相应的值替换：原先只会是null
        this.useValueList.set(index, irValue);
        // 添加use关系
        irValue.AddUse(new IrUse(this, irValue));
    }

    public void RemoveBlock(IrBasicBlock irBasicBlock) {
        int index = this.beforeBlockList.indexOf(irBasicBlock);
        // 进行相应的值替换
        if (index >= 0) {
            IrValue oldValue = this.useValueList.get(index);
            this.useValueList.remove(index);
            this.beforeBlockList.remove(index);
            if (oldValue != null) {
                oldValue.DeleteUser(this);
            }
        }
    }

    public void ReplaceBlock(IrBasicBlock oldBlock, IrBasicBlock newBlock) {
        int index;
        if (this.beforeBlockList.contains(newBlock)) {
            index = this.beforeBlockList.indexOf(newBlock);
            this.beforeBlockList.remove(index);
            this.useValueList.remove(index);
        }

        index = this.beforeBlockList.indexOf(oldBlock);
        if (index >= 0) {
            this.beforeBlockList.set(index, newBlock);
        }
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
            blockBuilder.append(this.useValueList.get(i).GetIrName());
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
