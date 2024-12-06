package midend.llvm.instr;

import midend.llvm.IrBuilder;
import midend.llvm.type.IrBaseType;
import midend.llvm.use.IrUse;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrValue;

import java.util.ArrayList;

public class PhiInstr extends Instr {
    private final ArrayList<IrBasicBlock> beforeBlockList;

    public PhiInstr(IrBasicBlock irBasicBlock) {
        super(IrBaseType.INT32, InstrType.PHI,
            IrBuilder.GetLocalVarName(irBasicBlock.GetIrFunction()), false);
        this.beforeBlockList = irBasicBlock.GetBeforeBlock();
        this.SetInBasicBlock(irBasicBlock);
        // 填充相应的value，等待后续替换
        for (int i = 0; i < this.beforeBlockList.size(); i++) {
            this.AddUseValue(null);
        }
    }

    public void ConvertBlockToValue(IrValue irValue, IrBasicBlock beforeBlock) {
        int index = this.beforeBlockList.indexOf(beforeBlock);
        // 进行相应的值替换
        this.useValueList.set(index, irValue);
        // 添加use关系
        irValue.AddUse(new IrUse(this, irValue));
    }

    @Override
    public String toString() {
        throw new RuntimeException("phi not finished yet");
    }
}
