package midend.llvm.type;

import midend.llvm.instr.ExtendInstr;
import midend.llvm.instr.TruncInstr;
import midend.llvm.value.IrValue;

public abstract class IrType {
    // 每一个IR的Value都有一个type
    // TODO：现在的实现太丑了，可以进行优化

    public boolean IsArrayType() {
        return this instanceof IrArrayType;
    }

    public boolean IsInt1Type() {
        return this == IrBaseType.INT1;
    }

    public boolean IsInt8Type() {
        return this == IrBaseType.INT8;
    }

    public boolean IsInt32Type() {
        return this == IrBaseType.INT32;
    }

    public boolean IsVoidType() {
        return this == IrBaseType.VOID;
    }

    public boolean IsPointerType() {
        return this instanceof IrPointerType;
    }

    public boolean IsBasicBlockType() {
        return this instanceof IrBasicBlockType;
    }

    public boolean IsFunctionType() {
        return this instanceof IrFunctionType;
    }

    public abstract String toString();

    public static IrValue ConvertType(IrValue originValue, IrType targetType) {
        IrType originType = originValue.GetIrType();
        if (targetType.IsInt32Type()) {
            if (originType.IsInt32Type()) {
                return originValue;
            } else {
                return new ExtendInstr(originValue, targetType);
            }
        } else if (targetType.IsInt8Type()) {
            if (originType.IsInt32Type()) {
                return new TruncInstr(originValue, targetType);
            } else if (originType.IsInt8Type()) {
                return originValue;
            } else {
                return new ExtendInstr(originValue, targetType);
            }
        } else if (targetType.IsInt1Type()) {
            if (originType.IsInt1Type()) {
                return originValue;
            } else {
                return new TruncInstr(originValue, targetType);
            }
        } else if (targetType.IsArrayType()) {
            IrArrayType arrayType = (IrArrayType) targetType;
            return ConvertType(originValue, arrayType.GetElementType());
        }
        return originValue;
    }
}
