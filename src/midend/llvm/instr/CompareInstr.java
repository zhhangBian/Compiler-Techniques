package midend.llvm.instr;

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
        return this.GetValueL().GetIrName() + " " +
            compareOp.toString().toLowerCase() + " " +
            this.GetValueR().GetIrName();
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
}
