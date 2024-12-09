package optimize;

import midend.llvm.value.IrFunction;

public class AllocateRegister extends Optimizer {
    @Override
    public void Optimize() {
        for (IrFunction irFunction : irModule.GetFunctions()) {
            RegisterAllocator allocator = new RegisterAllocator(irFunction);
            // 从起始开始分配
            allocator.Allocate(irFunction.GetBasicBlocks().get(0));
        }
    }
}
