package optimize;

import midend.llvm.IrBuilder;
import midend.llvm.instr.AluInstr;
import midend.llvm.instr.CallInstr;
import midend.llvm.instr.CompareInstr;
import midend.llvm.instr.ExtendInstr;
import midend.llvm.instr.Instr;
import midend.llvm.instr.ReturnInstr;
import midend.llvm.instr.TruncInstr;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;
import midend.llvm.value.IrValue;

import java.util.ArrayList;
import java.util.HashMap;

/** 只内联无副作用、单基本块的小函数，避免复制控制流和 Phi。 */
public class InlineSmallFunction extends Optimizer {
    private static final int MAX_INSTRS = 8;

    @Override
    public void Optimize() {
        for (IrFunction caller : irModule.GetFunctions()) {
            for (IrBasicBlock block : caller.GetBasicBlocks()) {
                for (Instr instr : new ArrayList<>(block.GetInstrList())) {
                    if (instr instanceof CallInstr call &&
                        this.CanInline(caller, call.GetTargetFunction())) {
                        this.Inline(caller, block, call);
                    }
                }
            }
        }
    }

    private boolean CanInline(IrFunction caller, IrFunction callee) {
        if (caller == callee || callee.GetBasicBlocks().size() != 1 ||
            callee.GetReturnType().IsVoidType()) {
            return false;
        }
        ArrayList<Instr> instrs = callee.GetBasicBlocks().get(0).GetInstrList();
        if (instrs.isEmpty() || instrs.size() > MAX_INSTRS ||
            !(instrs.get(instrs.size() - 1) instanceof ReturnInstr)) {
            return false;
        }
        for (int i = 0; i < instrs.size() - 1; i++) {
            Instr instr = instrs.get(i);
            if (!(instr instanceof AluInstr || instr instanceof CompareInstr ||
                instr instanceof ExtendInstr || instr instanceof TruncInstr)) {
                return false;
            }
        }
        return true;
    }

    private void Inline(IrFunction caller, IrBasicBlock block, CallInstr call) {
        IrFunction callee = call.GetTargetFunction();
        HashMap<IrValue, IrValue> values = new HashMap<>();
        for (int i = 0; i < callee.GetParameterList().size(); i++) {
            values.put(callee.GetParameterList().get(i), call.GetParamList().get(i));
        }

        IrBasicBlock staging = new IrBasicBlock("inline_stage", caller);
        IrBuilder.BuildInContext(caller, staging, () -> {
            for (Instr instr : callee.GetBasicBlocks().get(0).GetInstrList()) {
                if (instr instanceof ReturnInstr ret) {
                    call.ModifyAllUsersToNewValue(this.Map(values, ret.GetReturnValue()));
                    break;
                }
                Instr copy = this.Copy(instr, values);
                values.put(instr, copy);
            }
        });

        int index = block.GetInstrList().indexOf(call);
        block.GetInstrList().addAll(index, staging.GetInstrList());
        for (Instr copy : staging.GetInstrList()) {
            copy.SetInBasicBlock(block);
        }
        call.RemoveAllValueUse();
        block.GetInstrList().remove(call);
    }

    private IrValue Map(HashMap<IrValue, IrValue> values, IrValue value) {
        return values.getOrDefault(value, value);
    }

    private Instr Copy(Instr instr, HashMap<IrValue, IrValue> values) {
        if (instr instanceof AluInstr alu) {
            String op = switch (alu.GetAluOp()) {
                case ADD -> "+";
                case SUB -> "-";
                case MUL -> "*";
                case SDIV -> "/";
                case SREM -> "%";
                case AND -> "&";
                case OR -> "|";
            };
            return new AluInstr(op, this.Map(values, alu.GetValueL()),
                this.Map(values, alu.GetValueR()));
        }
        if (instr instanceof CompareInstr compare) {
            String op = switch (compare.GetCompareOp()) {
                case EQ -> "==";
                case NE -> "!=";
                case SGT -> ">";
                case SGE -> ">=";
                case SLT -> "<";
                case SLE -> "<=";
            };
            return new CompareInstr(op, this.Map(values, compare.GetValueL()),
                this.Map(values, compare.GetValueR()));
        }
        if (instr instanceof ExtendInstr extend) {
            return new ExtendInstr(this.Map(values, extend.GetOriginValue()),
                extend.GetTargetType());
        }
        TruncInstr trunc = (TruncInstr) instr;
        return new TruncInstr(this.Map(values, trunc.GetOriginValue()),
            trunc.GetTargetType());
    }
}
