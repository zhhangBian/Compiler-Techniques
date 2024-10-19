package midend.llvm.value.function;

import midend.llvm.IrNode;

public class IrFunction extends IrNode {
    // 一切运行单元皆function
    @Override
    public String toString() {
        throw new RuntimeException();
    }
}
