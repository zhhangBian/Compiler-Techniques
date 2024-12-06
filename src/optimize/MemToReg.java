package optimize;

import midend.llvm.instr.AllocateInstr;
import midend.llvm.instr.Instr;
import midend.llvm.type.IrArrayType;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;

import java.util.ArrayList;

public class MemToReg extends Optimizer {
    @Override
    public void Optimize() {
        for (IrFunction irFunction : irModule.GetFunctions()) {
            for (IrBasicBlock irBasicBlock : irFunction.GetBasicBlocks()) {
                ArrayList<Instr> instrList = new ArrayList<>(irBasicBlock.GetInstrList());
                for (Instr instr : instrList) {
                    if (this.IsValueAllocate(instr)) {
                        InstrAddPhi addPhi =
                            new InstrAddPhi((AllocateInstr) instr, instr.GetInBasicBlock());
                        addPhi.AddPhi();
                    }
                }
            }
        }
    }

    private boolean IsValueAllocate(Instr instr) {
        // 只对非数组类型添加phi
        return instr instanceof AllocateInstr allocateInstr &&
            !(allocateInstr.GetTargetType() instanceof IrArrayType);
    }
}
