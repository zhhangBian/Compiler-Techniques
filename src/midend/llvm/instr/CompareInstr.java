package midend.llvm.instr;

import backend.mips.Register;
import backend.mips.assembly.MipsCompare;
import midend.llvm.constant.IrConstant;
import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrValue;
import utils.Setting;

public class CompareInstr extends Instr {
    public enum CompareOp {
        EQ,
        NE,
        SGT,
        SGE,
        SLT,
        SLE
    }

    private final CompareOp compareOp;

    public CompareInstr(String op, IrValue valueL, IrValue valueR) {
        super(IrBaseType.INT1, InstrType.CMP);
        this.compareOp = this.GetStringToCompareOp(op);
        this.AddUseValue(valueL);
        this.AddUseValue(valueR);
    }

    public CompareOp GetCompareOp() {
        return this.compareOp;
    }

    public IrValue GetValueL() {
        return this.useValueList.get(0);
    }

    public IrValue GetValueR() {
        return this.useValueList.get(1);
    }

    @Override
    public boolean DefValue() {
        return true;
    }

    @Override
    public String GetGvnHash() {
        String valueL = this.GetValueL().GetIrName();
        String valueR = this.GetValueR().GetIrName();
        // 按照字典序
        return valueR + " " + this.compareOp + " " + valueL;
    }

    @Override
    public String toString() {
        return this.irName + " = icmp " +
            compareOp.toString().toLowerCase() + " i32 " +
            this.GetValueL().GetIrName() + ", " +
            this.GetValueR().GetIrName();
    }

    @Override
    public void toMips() {
        super.toMips();

        IrValue valueL = this.GetValueL();
        IrValue valueR = this.GetValueR();

        Register registerL = this.GetRegisterOrK0ForValue(valueL);
        Register registerR = this.GetRegisterOrK1ForValue(valueR);
        Register registerResult = this.GetRegisterOrK0ForValue(this);

        if (Setting.FINE_TUNING) {
            if (valueR instanceof IrConstant irConstantR) {
                this.LoadValueToRegister(valueL, registerL);
                this.GenerateMipsCompareInstr(registerL, irConstantR, registerResult);
            } else {
                // 加载数据
                this.LoadValueToRegister(valueL, registerL);
                this.LoadValueToRegister(valueR, registerR);

                // 生成计算指令
                this.GenerateMipsCompareInstr(registerL, registerR, registerResult);
            }
        } else {
            // 加载数据
            this.LoadValueToRegister(valueL, registerL);
            this.LoadValueToRegister(valueR, registerR);

            // 生成计算指令
            this.GenerateMipsCompareInstr(registerL, registerR, registerResult);
        }

        // 如果没有寄存器保留结果，则应该把结果存到栈上
        this.SaveRegisterResult(this, registerResult);
    }

    private CompareOp GetStringToCompareOp(String op) {
        return switch (op) {
            case "==" -> CompareOp.EQ;
            case "!=" -> CompareOp.NE;
            case ">=" -> CompareOp.SGE;
            case ">" -> CompareOp.SGT;
            case "<=" -> CompareOp.SLE;
            case "<" -> CompareOp.SLT;
            default -> throw new RuntimeException("illegal compare op");
        };
    }

    private void GenerateMipsCompareInstr(Register registerL, Register registerR,
                                          Register registerResult) {
        switch (this.compareOp) {
            case EQ ->
                new MipsCompare(MipsCompare.CompareType.SEQ, registerResult, registerL, registerR);
            case NE ->
                new MipsCompare(MipsCompare.CompareType.SNE, registerResult, registerL, registerR);
            case SGT ->
                new MipsCompare(MipsCompare.CompareType.SGT, registerResult, registerL, registerR);
            case SGE ->
                new MipsCompare(MipsCompare.CompareType.SGE, registerResult, registerL, registerR);
            case SLT ->
                new MipsCompare(MipsCompare.CompareType.SLT, registerResult, registerL, registerR);
            case SLE ->
                new MipsCompare(MipsCompare.CompareType.SLE, registerResult, registerL, registerR);
            default -> throw new RuntimeException("illegal compare op");
        }
    }

    private void GenerateMipsCompareInstr(
        Register registerL, IrConstant irConstant, Register registerResult) {
        int immediate = Integer.parseInt(irConstant.GetIrName());
        switch (this.compareOp) {
            case EQ -> new MipsCompare(MipsCompare.CompareType.SEQ,
                registerResult, registerL, immediate);
            case NE -> new MipsCompare(MipsCompare.CompareType.SNE,
                registerResult, registerL, immediate);
            case SGT -> new MipsCompare(MipsCompare.CompareType.SGT,
                registerResult, registerL, immediate);
            case SGE -> new MipsCompare(MipsCompare.CompareType.SGE,
                registerResult, registerL, immediate);
            case SLT -> new MipsCompare(MipsCompare.CompareType.SLTI,
                registerResult, registerL, immediate);
            case SLE -> new MipsCompare(MipsCompare.CompareType.SLE,
                registerResult, registerL, immediate);
            default -> throw new RuntimeException("illegal compare op");
        }
    }
}
