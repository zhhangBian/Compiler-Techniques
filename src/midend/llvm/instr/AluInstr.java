package midend.llvm.instr;

import backend.mips.Register;
import backend.mips.assembly.MipsAlu;
import backend.mips.assembly.MipsMdu;
import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrValue;

public class AluInstr extends Instr {
    public enum AluType {
        ADD,
        SUB,
        AND,
        OR,
        MUL,
        SDIV,
        SREM
    }

    private final AluType aluType;

    public AluInstr(String aluOp, IrValue valueL, IrValue valueR) {
        super(IrBaseType.INT32, InstrType.ALU);
        this.aluType = this.GetAluType(aluOp);
        this.AddUseValue(valueL);
        this.AddUseValue(valueR);
    }

    @Override
    public boolean DefValue() {
        return true;
    }

    @Override
    public String toString() {
        IrValue irValueL = this.GetValueL();
        IrValue irValueR = this.GetValueR();

        return this.irName + " = " + this.aluType.toString().toLowerCase() +
            " i32 " + irValueL.GetIrName() + ", " + irValueR.GetIrName();
    }

    @Override
    public void toMips() {
        super.toMips();

        IrValue valueL = this.GetValueL();
        IrValue valueR = this.GetValueR();

        Register registerL = Register.K0;
        Register registerR = Register.K1;
        LoadValueToRegister(valueL, registerL);
        LoadValueToRegister(valueR, registerR);

        // 为计算结果分配寄存器
        Register registerResult = this.GetRegisterOrK0ForValue(this);

        // 生成计算指令
        this.GenerateAluMipsInstr(registerL, registerR, registerResult);

        // 如果没有寄存器保留结果，则应该把结果存到栈上
        this.SaveResult(this, registerResult);
    }

    private IrValue GetValueL() {
        return this.useValueList.get(0);
    }

    private IrValue GetValueR() {
        return this.useValueList.get(1);
    }

    private AluType GetAluType(String aluOp) {
        return switch (aluOp) {
            case "+" -> AluType.ADD;
            case "-" -> AluType.SUB;
            case "*" -> AluType.MUL;
            case "/" -> AluType.SDIV;
            case "%" -> AluType.SREM;
            case "&", "&&" -> AluType.AND;
            case "|", "||" -> AluType.OR;
            default -> throw new RuntimeException("illegal alu op");
        };
    }

    private void GenerateAluMipsInstr(Register registerL, Register registerR,
                                      Register registerResult) {
        switch (this.aluType) {
            case ADD -> new MipsAlu(MipsAlu.AluType.ADDU, registerResult, registerL, registerR);
            case SUB -> new MipsAlu(MipsAlu.AluType.SUBU, registerResult, registerL, registerR);
            case AND -> new MipsAlu(MipsAlu.AluType.AND, registerResult, registerL, registerR);
            case OR -> new MipsAlu(MipsAlu.AluType.OR, registerResult, registerL, registerR);
            case MUL -> {
                new MipsMdu(MipsMdu.MduType.MULT, registerL, registerR);
                new MipsMdu(MipsMdu.MduType.MFLO, registerResult);
            }
            case SDIV -> {
                new MipsMdu(MipsMdu.MduType.DIV, registerL, registerR);
                new MipsMdu(MipsMdu.MduType.MFLO, registerResult);
            }
            case SREM -> {
                new MipsMdu(MipsMdu.MduType.DIV, registerL, registerR);
                new MipsMdu(MipsMdu.MduType.MFHI, registerResult);
            }
            default -> throw new RuntimeException("illegal alu type");
        }
    }
}
