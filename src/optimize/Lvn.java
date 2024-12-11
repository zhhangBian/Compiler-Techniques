package optimize;

import midend.llvm.constant.IrConstant;
import midend.llvm.constant.IrConstantInt;
import midend.llvm.instr.AluInstr;
import midend.llvm.instr.CompareInstr;
import midend.llvm.instr.ExtendInstr;
import midend.llvm.instr.GepInstr;
import midend.llvm.instr.Instr;
import midend.llvm.instr.MoveInstr;
import midend.llvm.instr.TruncInstr;
import midend.llvm.type.IrType;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;
import midend.llvm.value.IrValue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Lvn extends Optimizer {
    private final HashMap<String, Instr> gvnHashMap;

    public Lvn() {
        this.gvnHashMap = new HashMap<>();
    }

    @Override
    public void Optimize() {
        for (IrFunction irFunction : irModule.GetFunctions()) {
            // 在每个function开始前初始化
            this.gvnHashMap.clear();
            // 开始遍历
            this.GvnVisit(irFunction.GetBasicBlocks().get(0));
        }
    }

    private void GvnVisit(IrBasicBlock irBasicBlock) {
        // 常量折叠
        this.FoldValue(irBasicBlock);

        // 当前block插入map的instr：在支配块中可使用
        HashSet<Instr> gvnAddInstrSet = new HashSet<>();
        this.FoldInstr(irBasicBlock, gvnAddInstrSet);

        // 对支配块遍历：支配快依然可折叠
        for (IrBasicBlock dominateBlock : irBasicBlock.GetDirectDominateBlocks()) {
            this.GvnVisit(dominateBlock);
        }

        // 恢复对当前的gvn-map，变量兄弟结点
        for (Instr addedInstr : gvnAddInstrSet) {
            this.gvnHashMap.remove(addedInstr.GetGvnHash());
        }
    }

    private boolean CanGvnInstr(Instr instr) {
        return instr instanceof AluInstr || instr instanceof CompareInstr ||
            instr instanceof GepInstr;
    }

    // 进行常量折叠
    private void FoldValue(IrBasicBlock irBasicBlock) {
        Iterator<Instr> iterator = irBasicBlock.GetInstrList().iterator();
        while (iterator.hasNext()) {
            Instr instr = iterator.next();
            boolean folded =
                instr instanceof AluInstr aluInstr ? this.FoldAluInstr(aluInstr) :
                    instr instanceof CompareInstr compareInstr ? this.FoldCompare(compareInstr) :
                        instr instanceof ExtendInstr extendInstr ? this.FoldExtend(extendInstr) :
                            instr instanceof TruncInstr truncInstr ? this.FoldTrunc(truncInstr) :
                                false;
            if (folded) {
                iterator.remove();
            }
        }
    }

    // 进行表达式替换
    private void FoldInstr(IrBasicBlock irBasicBlock, HashSet<Instr> addedInstr) {
        Iterator<Instr> iterator = irBasicBlock.GetInstrList().iterator();
        while (iterator.hasNext()) {
            Instr instr = iterator.next();
            if (this.CanGvnInstr(instr)) {
                String hash = instr.GetGvnHash();
                // 如果存在，则替换值
                if (this.gvnHashMap.containsKey(hash)) {
                    instr.ModifyAllUsersToNewValue(this.gvnHashMap.get(hash));
                    iterator.remove();
                }
                // else，插入map
                else {
                    this.gvnHashMap.put(hash, instr);
                    addedInstr.add(instr);
                }
            }
            // 对于move
            else if (instr instanceof MoveInstr moveInstr) {
                IrValue dstValue = moveInstr.GetDstValue();
                IrValue srcValue = moveInstr.GetSrcValue();
                dstValue.ModifyAllUsersToNewValue(srcValue);
            }
        }
    }

    // alu
    private boolean FoldAluInstr(AluInstr aluInstr) {
        IrValue valueL = aluInstr.GetValueL();
        IrValue valueR = aluInstr.GetValueR();
        // 两个常量
        if (valueL instanceof IrConstant && valueR instanceof IrConstant) {
            return this.FoldAluTwoConstant(valueL, valueR, aluInstr);
        }
        // 一个常量
        else {
            return this.FoldAluElseConstant(valueL, valueR, aluInstr);
        }
    }

    // 折叠两个常量
    private boolean FoldAluTwoConstant(IrValue valueL, IrValue valueR, AluInstr aluInstr) {
        int numL = Integer.parseInt(valueL.GetIrName());
        int numR = Integer.parseInt(valueR.GetIrName());
        int num = switch (aluInstr.GetAluOp()) {
            case ADD -> numL + numR;
            case SUB -> numL - numR;
            case AND -> numL & numR;
            case OR -> numL | numR;
            case MUL -> numL * numR;
            case SDIV -> numL / numR;
            case SREM -> numL % numR;
        };

        IrConstant resultConstant = new IrConstantInt(num);
        aluInstr.ModifyAllUsersToNewValue(resultConstant);
        return true;
    }

    // 折叠一个或没有常量
    private boolean FoldAluElseConstant(IrValue valueL, IrValue valueR, AluInstr aluInstr) {
        return switch (aluInstr.GetAluOp()) {
            case ADD -> this.FoldElseAdd(valueL, valueR, aluInstr);
            case SUB -> this.FoldElseSub(valueL, valueR, aluInstr);
            case MUL -> this.FoldElseMul(valueL, valueR, aluInstr);
            case SDIV -> this.FoldElseDiv(valueL, valueR, aluInstr);
            case SREM -> this.FoldElseRem(valueL, valueR, aluInstr);
            case AND -> this.FoldElseAnd(valueL, valueR, aluInstr);
            case OR -> this.FoldElseOr(valueL, valueR, aluInstr);
        };
    }

    private boolean FoldElseAdd(IrValue valueL, IrValue valueR, AluInstr aluInstr) {
        if (valueL instanceof IrConstant && Integer.parseInt(valueL.GetIrName()) == 0) {
            aluInstr.ModifyAllUsersToNewValue(valueR);
            return true;
        } else if (valueR instanceof IrConstant && Integer.parseInt(valueR.GetIrName()) == 0) {
            aluInstr.ModifyAllUsersToNewValue(valueL);
            return true;
        }
        return false;
    }

    private boolean FoldElseSub(IrValue valueL, IrValue valueR, AluInstr aluInstr) {
        if (valueR instanceof IrConstant && Integer.parseInt(valueR.GetIrName()) == 0) {
            aluInstr.ModifyAllUsersToNewValue(valueL);
            return true;
        } else if (valueR == valueL) {
            aluInstr.ModifyAllUsersToNewValue(new IrConstantInt(0));
            return true;
        }
        return false;
    }

    private boolean FoldElseMul(IrValue valueL, IrValue valueR, AluInstr aluInstr) {
        if (valueL instanceof IrConstant) {
            int constant = Integer.parseInt(valueL.GetIrName());
            if (constant == 0) {
                aluInstr.ModifyAllUsersToNewValue(new IrConstantInt(0));
                return true;
            } else if (constant == 1) {
                aluInstr.ModifyAllUsersToNewValue(valueR);
                return true;
            }
        } else if (valueR instanceof IrConstant) {
            int constant = Integer.parseInt(valueR.GetIrName());
            if (constant == 0) {
                aluInstr.ModifyAllUsersToNewValue(new IrConstantInt(0));
                return true;
            } else if (constant == 1) {
                aluInstr.ModifyAllUsersToNewValue(valueL);
                return true;
            }
        }
        return false;
    }

    private boolean FoldElseDiv(IrValue valueL, IrValue valueR, AluInstr aluInstr) {
        if (valueR instanceof IrConstant) {
            if (Integer.parseInt(valueR.GetIrName()) == 1) {
                aluInstr.ModifyAllUsersToNewValue(valueL);
                return true;
            }
        }
        // 两个值相等
        else if (valueL == valueR) {
            aluInstr.ModifyAllUsersToNewValue(new IrConstantInt(1));
            return true;
        }
        return false;
    }

    private boolean FoldElseAnd(IrValue valueL, IrValue valueR, AluInstr aluInstr) {
        if (valueR instanceof IrConstant) {
            if (Integer.parseInt(valueR.GetIrName()) == 0) {
                aluInstr.ModifyAllUsersToNewValue(new IrConstantInt(0));
                return true;
            }
        } else if (valueL instanceof IrConstant) {
            if (Integer.parseInt(valueL.GetIrName()) == 0) {
                aluInstr.ModifyAllUsersToNewValue(new IrConstantInt(0));
                return true;
            }
        }
        return false;
    }

    private boolean FoldElseOr(IrValue valueL, IrValue valueR, AluInstr aluInstr) {
        if (valueR instanceof IrConstant) {
            if (Integer.parseInt(valueR.GetIrName()) == 1) {
                aluInstr.ModifyAllUsersToNewValue(new IrConstantInt(1));
                return true;
            }
        } else if (valueL instanceof IrConstant) {
            if (Integer.parseInt(valueL.GetIrName()) == 0) {
                aluInstr.ModifyAllUsersToNewValue(new IrConstantInt(1));
                return true;
            }
        }
        return false;
    }

    private boolean FoldElseRem(IrValue valueL, IrValue valueR, AluInstr aluInstr) {
        if (valueR instanceof IrConstant) {
            if (Integer.parseInt(valueR.GetIrName()) == 1) {
                aluInstr.ModifyAllUsersToNewValue(new IrConstantInt(0));
                return true;
            }
        }
        // 两个值相等
        else if (valueL == valueR) {
            aluInstr.ModifyAllUsersToNewValue(new IrConstantInt(0));
            return true;
        }
        return false;
    }

    // compare
    private boolean FoldCompare(CompareInstr compareInstr) {
        IrValue valueL = compareInstr.GetValueL();
        IrValue valueR = compareInstr.GetValueR();

        if (valueL instanceof IrConstant && valueR instanceof IrConstant) {
            return this.FoldCompareTwoConstant(valueL, valueR, compareInstr);
        }
        return false;
    }

    // 折叠两个常量
    private boolean FoldCompareTwoConstant(IrValue valueL, IrValue valueR,
                                           CompareInstr compareInstr) {
        int numL = Integer.parseInt(valueL.GetIrName());
        int numR = Integer.parseInt(valueR.GetIrName());
        int num = switch (compareInstr.GetCompareOp()) {
            case EQ -> numL == numR;
            case NE -> numL != numR;
            case SGT -> numL > numR;
            case SGE -> numL >= numR;
            case SLT -> numL < numR;
            case SLE -> numL <= numR;
        } ? 1 : 0;

        IrConstant resultConstant = new IrConstantInt(num);
        compareInstr.ModifyAllUsersToNewValue(resultConstant);
        return true;
    }

    // 折叠一个或没有常量
    private boolean FoldCompareElseConstant(IrValue valueL, IrValue valueR,
                                            CompareInstr compareInstr) {
        return switch (compareInstr.GetCompareOp()) {
            case EQ -> this.FoldElseEq(valueL, valueR, compareInstr);
            case NE -> this.FoldElseNe(valueL, valueR, compareInstr);
            case SGT -> this.FoldElseSgt(valueL, valueR, compareInstr);
            case SGE -> this.FoldElseSge(valueL, valueR, compareInstr);
            case SLT -> this.FoldElseSlt(valueL, valueR, compareInstr);
            case SLE -> this.FoldElseSle(valueL, valueR, compareInstr);
        };
    }

    private boolean FoldElseEq(IrValue valueL, IrValue valueR, CompareInstr compareInstr) {
        if (valueR == valueL) {
            compareInstr.ModifyAllUsersToNewValue(new IrConstantInt(1));
            return true;
        }
        return false;
    }

    private boolean FoldElseNe(IrValue valueL, IrValue valueR, CompareInstr compareInstr) {
        if (valueR == valueL) {
            compareInstr.ModifyAllUsersToNewValue(new IrConstantInt(0));
            return true;
        }
        return false;
    }

    private boolean FoldElseSgt(IrValue valueL, IrValue valueR, CompareInstr compareInstr) {
        if (valueR == valueL) {
            compareInstr.ModifyAllUsersToNewValue(new IrConstantInt(0));
            return true;
        }
        return false;
    }

    private boolean FoldElseSge(IrValue valueL, IrValue valueR, CompareInstr compareInstr) {
        if (valueR == valueL) {
            compareInstr.ModifyAllUsersToNewValue(new IrConstantInt(1));
            return true;
        }
        return false;
    }

    private boolean FoldElseSlt(IrValue valueL, IrValue valueR, CompareInstr compareInstr) {
        if (valueR == valueL) {
            compareInstr.ModifyAllUsersToNewValue(new IrConstantInt(0));
            return true;
        }
        return false;
    }

    private boolean FoldElseSle(IrValue valueL, IrValue valueR, CompareInstr compareInstr) {
        if (valueR == valueL) {
            compareInstr.ModifyAllUsersToNewValue(new IrConstantInt(1));
            return true;
        }
        return false;
    }

    // extend
    private boolean FoldExtend(ExtendInstr extendInstr) {
        IrType targetType = extendInstr.GetTargetType();
        IrType originType = extendInstr.GetOriginType();
        IrValue originValue = extendInstr.GetOriginValue();

        if (targetType == originType) {
            extendInstr.ModifyAllUsersToNewValue(originValue);
            return true;
        }
        return false;
    }

    // trunc
    private boolean FoldTrunc(TruncInstr truncInstr) {
        IrType targetType = truncInstr.GetTargetType();
        IrType originType = truncInstr.GetOriginType();
        IrValue originValue = truncInstr.GetOriginValue();

        if (targetType == originType) {
            truncInstr.ModifyAllUsersToNewValue(originValue);
            return true;
        }
        return false;
    }
}
