package midend.llvm.constant;

import backend.mips.assembly.data.MipsWord;
import midend.llvm.type.IrBaseType;

public class IrConstantInt extends IrConstant {
    private final int value;

    public IrConstantInt(int value) {
        super(IrBaseType.INT32, String.valueOf(value));
        this.value = value;
    }

    public int GetValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "i32 " + this.value;
    }

    @Override
    public void toMips() {
        this.MipsDeclare(this.GetMipsLabel());
    }

    @Override
    public void MipsDeclare(String label) {
        new MipsWord(label, this.value);
    }
}
