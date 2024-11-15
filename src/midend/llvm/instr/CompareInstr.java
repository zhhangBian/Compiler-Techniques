package midend.llvm.instr;

import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrValue;

public class CompareInstr extends Instr {
    public enum CompareOp {
        EQ,
        NE,
        GT,
        GE,
        LT,
        LE
    }

    private final CompareOp compareOp;

    public CompareInstr(String irName, String op, IrValue valueL, IrValue valueR) {
        super(IrBaseType.INT1, irName, InstrType.CMP);
        this.compareOp = this.GetCompareOp(op);
        this.AddUseValue(valueL);
        this.AddUseValue(valueR);
    }

    private CompareOp GetCompareOp(String op) {
        return switch (op) {
            case "==" -> CompareOp.EQ;
            case "!=" -> CompareOp.NE;
            case ">=" -> CompareOp.GE;
            case ">" -> CompareOp.GT;
            case "<=" -> CompareOp.LE;
            case "<" -> CompareOp.LT;
            default -> throw new RuntimeException("illegal compare op");
        };
    }
}
