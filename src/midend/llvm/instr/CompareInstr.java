package midend.llvm.instr;

import backend.mips.Register;
import backend.mips.assembly.MipsCompare;
import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrValue;

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
        this.compareOp = this.GetCompareOp(op);
        this.AddUseValue(valueL);
        this.AddUseValue(valueR);
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

        Register registerL = Register.K0;
        Register registerR = Register.K1;
        Register registerResult = this.GetRegisterOrK0ForValue(this);

        // 加载数据
        this.LoadValueToRegister(valueL, registerL);
        this.LoadValueToRegister(valueR, registerR);

        // 生成计算指令
        this.GenerateMipsCompareInstr(registerL, registerR, registerResult);

        // 如果没有寄存器保留结果，则应该把结果存到栈上
        this.SaveResult(this, registerResult);
    }

    private CompareOp GetCompareOp(String op) {
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

    private IrValue GetValueL() {
        return this.useValueList.get(0);
    }

    private IrValue GetValueR() {
        return this.useValueList.get(1);
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
}
