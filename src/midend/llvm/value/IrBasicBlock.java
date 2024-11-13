package midend.llvm.value;

import midend.llvm.type.IrBasicBlockType;

public class IrBasicBlock extends IrValue {
    private IrFunction parentFunction;

    public IrBasicBlock(String irName) {
        super(IrBasicBlockType.BASIC_BLOCK, irName);
        this.parentFunction = null;
    }

    public void SetParentFunction(IrFunction irFunction) {
        this.parentFunction = irFunction;
    }
}
