package optimize;

import midend.llvm.instr.AluInstr;
import midend.llvm.instr.CompareInstr;
import midend.llvm.instr.ExtendInstr;
import midend.llvm.instr.GepInstr;
import midend.llvm.instr.Instr;
import midend.llvm.instr.JumpInstr;
import midend.llvm.instr.TruncInstr;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;
import midend.llvm.value.IrValue;

import java.util.ArrayList;
import java.util.HashSet;

public class LoopInvariantCodeMotion extends Optimizer {
    @Override
    public void Optimize() {
        for (IrFunction function : irModule.GetFunctions()) {
            for (IrBasicBlock tail : function.GetBasicBlocks()) {
                for (IrBasicBlock header : tail.GetNextBlocks()) {
                    if (tail.GetDominatorBlocks().contains(header)) {
                        this.HoistLoop(header, tail, function);
                    }
                }
            }
        }
    }

    private void HoistLoop(IrBasicBlock header, IrBasicBlock tail, IrFunction function) {
        HashSet<IrBasicBlock> loop = new HashSet<>();
        loop.add(header);
        loop.add(tail);
        ArrayList<IrBasicBlock> workList = new ArrayList<>();
        if (tail != header) {
            workList.add(tail);
        }
        while (!workList.isEmpty()) {
            IrBasicBlock block = workList.remove(workList.size() - 1);
            for (IrBasicBlock predecessor : block.GetBeforeBlocks()) {
                if (loop.add(predecessor) && predecessor != header) {
                    workList.add(predecessor);
                }
            }
        }

        ArrayList<IrBasicBlock> outside = new ArrayList<>();
        for (IrBasicBlock predecessor : header.GetBeforeBlocks()) {
            if (!loop.contains(predecessor)) {
                outside.add(predecessor);
            }
        }
        if (outside.size() != 1) {
            return;
        }
        IrBasicBlock preheader = outside.get(0);
        if (preheader.GetNextBlocks().size() != 1 ||
            !(preheader.GetLastInstr() instanceof JumpInstr)) {
            return;
        }

        boolean changed;
        do {
            changed = false;
            for (IrBasicBlock block : function.GetBasicBlocks()) {
                if (!loop.contains(block)) {
                    continue;
                }
                for (Instr instr : new ArrayList<>(block.GetInstrList())) {
                    if (this.CanHoist(instr) && this.OperandsOutsideLoop(instr, loop)) {
                        block.GetInstrList().remove(instr);
                        preheader.AddInstrBeforeJump(instr);
                        changed = true;
                    }
                }
            }
        } while (changed);
    }

    private boolean CanHoist(Instr instr) {
        if (instr instanceof AluInstr alu) {
            return alu.GetAluOp() != AluInstr.AluType.SDIV &&
                alu.GetAluOp() != AluInstr.AluType.SREM;
        }
        return instr instanceof CompareInstr || instr instanceof ExtendInstr ||
            instr instanceof TruncInstr || instr instanceof GepInstr;
    }

    private boolean OperandsOutsideLoop(Instr instr, HashSet<IrBasicBlock> loop) {
        for (IrValue value : instr.GetUseValueList()) {
            if (value instanceof Instr input && loop.contains(input.GetInBasicBlock())) {
                return false;
            }
        }
        return true;
    }
}
