package midend.llvm.instr;

import midend.llvm.type.IrArrayType;
import midend.llvm.type.IrPointerType;
import midend.llvm.type.IrType;
import midend.llvm.value.IrValue;

public class GepInstr extends Instr {
    public GepInstr(IrValue pointer, IrValue offset) {
        super(new IrPointerType(GetTargetType(pointer)), InstrType.GEP);
        this.AddUseValue(pointer);
        this.AddUseValue(offset);
    }

    public IrValue GetPointer() {
        return this.useValueList.get(0);
    }

    public IrValue GetOffset() {
        return this.useValueList.get(1);
    }

    @Override
    public String toString() {
        IrValue pointer = this.GetPointer();
        IrValue offset = this.GetOffset();

        IrPointerType pointerType = (IrPointerType) pointer.GetIrType();
        IrArrayType targetType = (IrArrayType) pointerType.GetTargetType();

        return this.irName + " = getelementptr inbounds " +
            targetType + ", " +
            pointerType + " " +
            pointer.GetIrName() + ", i32 0, " +
            targetType.GetElementType() + " " +
            offset.GetIrName();
    }

    public static IrType GetTargetType(IrValue pointer) {
        IrType targetType = ((IrPointerType) pointer.GetIrType()).GetTargetType();
        if (targetType instanceof IrArrayType arrayType) {
            return arrayType.GetElementType();
        } else {
            return targetType;
        }
    }
}
