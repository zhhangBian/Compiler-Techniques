package optimize;

import midend.llvm.constant.IrConstant;
import midend.llvm.instr.AllocateInstr;
import midend.llvm.instr.GepInstr;
import midend.llvm.type.IrArrayType;
import midend.llvm.type.IrPointerType;
import midend.llvm.type.IrType;
import midend.llvm.value.IrGlobalValue;
import midend.llvm.value.IrValue;

/** 只证明显然不重叠的地址；其他情况一律视为可能别名。 */
final class MemoryAlias {
    private record Location(IrValue base, Integer offset) {
    }

    private MemoryAlias() {
    }

    static boolean MayAlias(IrValue left, IrValue right) {
        if (left == right) {
            return true;
        }
        Location a = Resolve(left);
        Location b = Resolve(right);
        if (a.base == b.base) {
            return a.offset == null || b.offset == null || a.offset.equals(b.offset);
        }
        return !IsUniqueBase(a.base) || !IsUniqueBase(b.base);
    }

    static boolean IsSafeFixedAddress(IrValue pointer) {
        Location location = Resolve(pointer);
        if (location.offset == null) {
            return false;
        }
        IrType targetType;
        if (location.base instanceof AllocateInstr allocate) {
            targetType = allocate.GetTargetType();
        } else if (location.base instanceof IrGlobalValue global) {
            targetType = ((IrPointerType) global.GetIrType()).GetTargetType();
        } else {
            return false;
        }
        return targetType instanceof IrArrayType array ?
            location.offset >= 0 && location.offset < array.GetArraySize() :
            location.offset == 0;
    }

    static IrValue GetBase(IrValue pointer) {
        return Resolve(pointer).base;
    }

    private static boolean IsUniqueBase(IrValue base) {
        return base instanceof AllocateInstr || base instanceof IrGlobalValue;
    }

    private static Location Resolve(IrValue pointer) {
        if (pointer instanceof GepInstr gep) {
            Location parent = Resolve(gep.GetPointer());
            if (parent.offset != null && gep.GetOffset() instanceof IrConstant constant) {
                return new Location(parent.base,
                    parent.offset + Integer.parseInt(constant.GetIrName()));
            }
            return new Location(parent.base, null);
        }
        return new Location(pointer, 0);
    }
}
