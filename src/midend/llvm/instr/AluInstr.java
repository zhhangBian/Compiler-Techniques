package midend.llvm.instr;

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
    public String toString() {
        IrValue irValueL = this.GetValueL();
        IrValue irValueR = this.GetValueR();

        return this.irName + " = " + this.aluType.toString().toLowerCase() +
            " i32 " + irValueL.GetIrName() + ", " + irValueR.GetIrName();
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
}
