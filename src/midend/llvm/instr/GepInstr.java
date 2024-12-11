package midend.llvm.instr;

import backend.mips.Register;
import backend.mips.assembly.MipsAlu;
import backend.mips.assembly.fake.MarsLi;
import midend.llvm.constant.IrConstant;
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
    public boolean DefValue() {
        return true;
    }

    @Override
    public String GetGvnHash() {
        // 按照字典序
        return this.GetPointer().GetIrName() + " " + this.GetOffset().GetIrName();
    }

    @Override
    public String toString() {
        IrValue pointer = this.GetPointer();
        IrValue offset = this.GetOffset();

        IrPointerType pointerType = (IrPointerType) pointer.GetIrType();
        IrType targetType = pointerType.GetTargetType();

        if (targetType.IsArrayType()) {
            IrArrayType arrayType = (IrArrayType) targetType;
            return this.irName + " = getelementptr inbounds " +
                arrayType + ", " +
                pointerType + " " +
                pointer.GetIrName() + ", i32 0, " +
                offset.GetIrType() + " " +
                offset.GetIrName();
        } else {
            return this.irName + " = getelementptr inbounds " +
                targetType + ", " +
                pointerType + " " +
                pointer.GetIrName() + ", " +
                offset.GetIrType() + " " +
                offset.GetIrName();
        }
    }

    @Override
    public void toMips() {
        super.toMips();

        IrValue pointerValue = this.GetPointer();
        IrValue offsetValue = this.GetOffset();

        Register pointerRegister = Register.K0;
        Register offsetRegister = Register.K1;

        // 加载数组的首地址
        this.LoadValueToRegister(pointerValue, pointerRegister);
        if (offsetValue instanceof IrConstant irConstant) {
            // 直接赋值
            new MarsLi(offsetRegister, Integer.parseInt(irConstant.GetIrName()) << 2);
        } else {
            // 加载offset的值
            this.LoadValueToRegister(offsetValue, offsetRegister);
            // 将offset左移两位
            new MipsAlu(MipsAlu.AluType.SLL, offsetRegister, offsetRegister, 2);
        }

        // 相加得到地址
        Register targetRegister = this.GetRegisterOrK0ForValue(this);
        new MipsAlu(MipsAlu.AluType.ADDU, targetRegister, Register.K1, pointerRegister);
        // 保存结果
        this.SaveRegisterResult(this, targetRegister);
    }

    public static IrType GetTargetType(IrValue pointer) {
        IrType targetType = ((IrPointerType) pointer.GetIrType()).GetTargetType();
        if (targetType instanceof IrArrayType arrayType) {
            return arrayType.GetElementType();
        } else if (targetType instanceof IrPointerType pointerType) {
            return pointerType.GetTargetType();
        } else {
            return targetType;
        }
    }
}
