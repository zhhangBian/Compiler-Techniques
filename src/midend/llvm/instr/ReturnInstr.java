package midend.llvm.instr;

import backend.mips.MipsBuilder;
import backend.mips.Register;
import backend.mips.assembly.MipsJump;
import backend.mips.assembly.fake.MarsMove;
import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrValue;

public class ReturnInstr extends Instr {
    public ReturnInstr(IrValue returnValue) {
        super(IrBaseType.VOID, InstrType.RETURN, "return");
        this.AddUseValue(returnValue);
    }

    public IrValue GetReturnValue() {
        return this.useValueList.isEmpty() ? null : this.useValueList.get(0);
    }

    @Override
    public boolean DefValue() {
        return false;
    }

    @Override
    public String toString() {
        IrValue returnValue = this.GetReturnValue();

        return "ret " + (returnValue == null ? "void" :
            returnValue.GetIrType() + " " + returnValue.GetIrName());
    }

    @Override
    public void toMips() {
        super.toMips();

        IrValue returnValue = this.GetReturnValue();
        // 不为void
        if (returnValue != null) {
            Register returnRegister = MipsBuilder.GetValueToRegister(returnValue);
            // 已经分配了寄存器
            if (returnRegister != null) {
                new MarsMove(Register.V0, returnRegister);
            }
            // 从内存中加载数据
            else {
                this.LoadValueToRegister(returnValue, Register.V0);
            }
        }
        new MipsJump(MipsJump.JumpType.JR, Register.RA);
    }
}
