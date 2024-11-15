package midend.llvm.instr;

import midend.llvm.IrBuilder;
import midend.llvm.type.IrType;
import midend.llvm.use.IrUser;

// instr是一种User：使用其他的Value作为参数
public class Instr extends IrUser {
    private final InstrType instrType;

    public Instr(IrType irType, String name, InstrType instrType) {
        super(irType, name);
        this.instrType = instrType;
        // 自动插入
        IrBuilder.AddInstr(this);
    }

    public InstrType GetInstrType() {
        return this.instrType;
    }
}
