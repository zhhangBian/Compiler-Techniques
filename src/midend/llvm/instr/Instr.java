package midend.llvm.instr;

import midend.llvm.type.IrType;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.use.IrUser;

// instr是一种User：使用其他的Value作为参数
public class Instr extends IrUser {
    private InstrType instrType;
    // 指令所处的基本块
    private IrBasicBlock basicBlock;

    public Instr(IrType irType, String name) {
        super(irType, name);
    }
}
