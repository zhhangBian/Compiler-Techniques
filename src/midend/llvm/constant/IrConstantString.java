package midend.llvm.constant;

import midend.llvm.type.IrArrayType;
import midend.llvm.type.IrBaseType;
import midend.llvm.type.IrPointerType;

public class IrConstantString extends IrConstant {
    private final String value;

    public IrConstantString(String name, String value) {
        super(new IrPointerType(new IrArrayType(value.length() + 1, IrBaseType.INT8)), name);
        this.value = value;
    }

    public String GetValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.irName + " = constant " +
            ((IrPointerType) this.irType).GetTargetType() + " c\"" + this.value + "\\00\"";
    }
}
