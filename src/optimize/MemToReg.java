package optimize;

import midend.llvm.instr.AllocateInstr;
import midend.llvm.instr.Instr;
import midend.llvm.type.IrArrayType;
import midend.llvm.type.IrPointerType;
import midend.llvm.type.IrType;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;

import java.util.ArrayList;

public class MemToReg extends Optimizer {
    @Override
    public void Optimize() {
        for (IrFunction irFunction : irModule.GetFunctions()) {
            IrBasicBlock entryBlock = irFunction.GetBasicBlocks().get(0);
            for (IrBasicBlock irBasicBlock : irFunction.GetBasicBlocks()) {
                ArrayList<Instr> instrList = new ArrayList<>(irBasicBlock.GetInstrList());
                for (Instr instr : instrList) {
                    if (this.IsValueAllocate(instr)) {
                        InsertPhi insertPhi =
                            new InsertPhi((AllocateInstr) instr, entryBlock);
                        insertPhi.AddPhi();
                    }
                }
            }
        }
    }

    private boolean IsValueAllocate(Instr instr) {
        // 只对非数组类型添加phi
        if (instr instanceof AllocateInstr allocateInstr) {
            IrType targetType = ((IrPointerType) allocateInstr.GetIrType()).GetTargetType();
            return !(targetType instanceof IrArrayType);
        }
        return false;
    }
}
