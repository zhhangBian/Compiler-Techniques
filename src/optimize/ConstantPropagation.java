package optimize;

import midend.llvm.constant.IrConstant;
import midend.llvm.constant.IrConstantInt;
import midend.llvm.instr.BranchInstr;
import midend.llvm.instr.Instr;
import midend.llvm.instr.JumpInstr;
import midend.llvm.instr.phi.PhiInstr;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;
import midend.llvm.value.IrValue;

import java.util.ArrayList;
import java.util.Iterator;

public class ConstantPropagation extends Optimizer {
    @Override
    public void Optimize() {
        boolean changed;
        do {
            changed = this.FoldPhi();
            int before = this.CountInstrs();
            new Lvn().Optimize();
            changed |= this.CountInstrs() != before;
            if (this.FoldBranches()) {
                new RemoveUnReachCode().Optimize();
                new CfgBuilder().Optimize();
                this.PrunePhiInputs();
                changed = true;
            }
        } while (changed);
    }

    private boolean FoldPhi() {
        boolean changed = false;
        for (IrFunction function : irModule.GetFunctions()) {
            for (IrBasicBlock block : function.GetBasicBlocks()) {
                Iterator<Instr> iterator = block.GetInstrList().iterator();
                while (iterator.hasNext()) {
                    Instr instr = iterator.next();
                    IrValue replacement = instr instanceof PhiInstr phi ?
                        this.GetPhiReplacement(phi) : null;
                    if (replacement != null && replacement != instr) {
                        instr.ModifyAllUsersToNewValue(replacement);
                        instr.RemoveAllValueUse();
                        iterator.remove();
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }

    private IrValue GetPhiReplacement(PhiInstr phi) {
        ArrayList<IrValue> inputs = phi.GetUseValueList();
        if (inputs.isEmpty()) {
            return null;
        }
        IrValue first = inputs.get(0);
        if (first != phi && inputs.stream().allMatch(value -> value == first)) {
            return first;
        }
        Integer constant = this.GetConstant(first);
        if (constant != null && inputs.stream().allMatch(
            value -> constant.equals(this.GetConstant(value)))) {
            return new IrConstantInt(constant);
        }
        return null;
    }

    private int CountInstrs() {
        int count = 0;
        for (IrFunction function : irModule.GetFunctions()) {
            for (IrBasicBlock block : function.GetBasicBlocks()) {
                count += block.GetInstrList().size();
            }
        }
        return count;
    }

    private Integer GetConstant(IrValue value) {
        return value instanceof IrConstant ? Integer.parseInt(value.GetIrName()) : null;
    }

    private boolean FoldBranches() {
        boolean changed = false;
        for (IrFunction function : irModule.GetFunctions()) {
            for (IrBasicBlock block : function.GetBasicBlocks()) {
                if (!(block.GetLastInstr() instanceof BranchInstr branch)) {
                    continue;
                }
                Integer condition = this.GetConstant(branch.GetCond());
                if (condition == null) {
                    continue;
                }
                IrBasicBlock target = condition != 0 ? branch.GetTrueBlock() : branch.GetFalseBlock();
                IrBasicBlock unused = condition != 0 ? branch.GetFalseBlock() : branch.GetTrueBlock();
                if (target != unused) {
                    for (Instr instr : unused.GetInstrList()) {
                        if (instr instanceof PhiInstr phi) {
                            phi.RemoveBlock(block);
                        }
                    }
                }
                block.ReplaceLastInstr(new JumpInstr(target, block));
                changed = true;
            }
        }
        return changed;
    }

    private void PrunePhiInputs() {
        for (IrFunction function : irModule.GetFunctions()) {
            for (IrBasicBlock block : function.GetBasicBlocks()) {
                for (Instr instr : block.GetInstrList()) {
                    if (!(instr instanceof PhiInstr phi)) {
                        break;
                    }
                    for (IrBasicBlock predecessor : new ArrayList<>(phi.GetBeforeBlockList())) {
                        if (!block.GetBeforeBlocks().contains(predecessor)) {
                            phi.RemoveBlock(predecessor);
                        }
                    }
                }
            }
        }
    }
}
