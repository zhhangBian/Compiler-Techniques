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
        DIV,
        MOD
    }

    private final AluType aluType;

    public AluInstr(String name, String aluOp, IrValue valueL, IrValue valueR) {
        super(IrBaseType.INT32, name, InstrType.ALU);
        this.aluType = this.GetAluType(aluOp);
        this.AddUseValue(valueL);
        this.AddUseValue(valueR);
    }

    public AluInstr(String name, AluType aluType, IrValue valueL, IrValue valueR) {
        super(IrBaseType.INT32, name, InstrType.ALU);
        this.aluType = aluType;
        this.AddUseValue(valueL);
        this.AddUseValue(valueR);
    }

    public AluType GetAluType() {
        return this.aluType;
    }

    private AluType GetAluType(String aluOp) {
        return switch (aluOp) {
            case "+" -> AluType.ADD;
            case "-" -> AluType.SUB;
            case "*" -> AluType.MUL;
            case "/" -> AluType.DIV;
            case "%" -> AluType.MOD;
            case "&", "&&" -> AluType.AND;
            case "|", "||" -> AluType.OR;
            default -> throw new RuntimeException("illegal alu op");
        };
    }
}
