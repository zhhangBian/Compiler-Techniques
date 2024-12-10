package optimize;

import midend.llvm.instr.BranchInstr;
import midend.llvm.instr.CallInstr;
import midend.llvm.instr.Instr;
import midend.llvm.instr.JumpInstr;
import midend.llvm.instr.ReturnInstr;
import midend.llvm.instr.StoreInstr;
import midend.llvm.instr.io.IoInstr;
import midend.llvm.instr.phi.PhiInstr;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;
import midend.llvm.value.IrValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

// 保守一些：与io、数组相关的都保留
public class RemoveDeadCode extends Optimizer {
    // 记录调用了哪些函数
    private final HashMap<IrFunction, HashSet<IrFunction>> calleeMap;
    // 记录被哪些函数调用
    private final HashMap<IrFunction, HashSet<IrFunction>> callerMap;
    // 副作用：有IO操作
    private final HashSet<IrFunction> sideEffectFunctions;

    public RemoveDeadCode() {
        this.calleeMap = new HashMap<>();
        this.callerMap = new HashMap<>();
        this.sideEffectFunctions = new HashSet<>();
    }

    @Override
    public void Optimize() {
        boolean finished = false;
        while (!finished) {
            finished = true;
            this.BuildFunctionCallMap();
            finished &= this.RemoveUselessBlock();
            finished &= this.RemoveUselessFunction();
            finished &= this.RemoveUselessCode();
            finished &= this.MergeBlock();
        }
    }

    private void BuildFunctionCallMap() {
        // 进行初始化
        this.calleeMap.clear();
        this.callerMap.clear();
        this.sideEffectFunctions.clear();
        for (IrFunction irFunction : irModule.GetFunctions()) {
            this.calleeMap.put(irFunction, new HashSet<>());
            this.callerMap.put(irFunction, new HashSet<>());
        }
        // 进行dfs
        HashSet<IrFunction> visited = new HashSet<>();
        this.DfsFunction(irModule.GetMainFunction(), visited);
    }

    private void DfsFunction(IrFunction visitFunction, HashSet<IrFunction> visited) {
        if (visited.contains(visitFunction)) {
            return;
        }
        visited.add(visitFunction);

        for (IrBasicBlock irBasicBlock : visitFunction.GetBasicBlocks()) {
            for (Instr instr : irBasicBlock.GetInstrList()) {
                // 函数调用
                if (instr instanceof CallInstr callInstr) {
                    IrFunction callee = callInstr.GetTargetFunction();
                    this.calleeMap.get(visitFunction).add(callee);
                    this.callerMap.get(callee).add(visitFunction);
                    // 对与函数副作用
                    if (this.sideEffectFunctions.contains(callee)) {
                        this.sideEffectFunctions.add(visitFunction);
                    }

                    this.DfsFunction(callee, visited);
                }
                // IO、存操作
                else if (instr instanceof IoInstr || instr instanceof StoreInstr) {
                    this.sideEffectFunctions.add(visitFunction);
                }
            }
        }
    }

    // 删除无用函数
    private boolean RemoveUselessFunction() {
        boolean finished = true;

        Iterator<IrFunction> iterator = irModule.GetFunctions().iterator();
        while (iterator.hasNext()) {
            IrFunction irFunction = iterator.next();
            // 无人调用，删除：即使有sideEffect也没关系
            if (!irFunction.IsMainFunction() && this.callerMap.get(irFunction).isEmpty()) {
                iterator.remove();
                finished = false;
            }
        }

        return finished;
    }

    // 删除无用基本块
    private boolean RemoveUselessBlock() {
        boolean finished = true;
        for (IrFunction irFunction : irModule.GetFunctions()) {
            Iterator<IrBasicBlock> iterator = irFunction.GetBasicBlocks().iterator();
            while (iterator.hasNext()) {
                IrBasicBlock visitBlock = iterator.next();
                // 不可达块，删除
                if (visitBlock.GetBeforeBlocks().isEmpty() && !visitBlock.IsEntryBlock()) {
                    // 删除指令
                    for (Instr instr : visitBlock.GetInstrList()) {
                        instr.RemoveAllValueUse();
                    }

                    // 改变关系
                    for (IrBasicBlock nextBlock : visitBlock.GetNextBlocks()) {
                        nextBlock.GetBeforeBlocks().remove(visitBlock);
                        // 消除phi
                        for (Instr nextInstr : nextBlock.GetInstrList()) {
                            if (nextInstr instanceof PhiInstr phiInstr) {
                                phiInstr.RemoveBlock(visitBlock);
                            }
                        }
                    }
                    finished = false;
                    iterator.remove();
                }
            }
        }
        return finished;
    }

    // 删除无用代码
    private boolean RemoveUselessCode() {
        boolean finished = true;
        HashSet<Instr> activeInstrSet = this.GetActiveInstrSet();

        for (IrFunction irFunction : irModule.GetFunctions()) {
            for (IrBasicBlock irBasicBlock : irFunction.GetBasicBlocks()) {
                Iterator<Instr> iterator = irBasicBlock.GetInstrList().iterator();
                while (iterator.hasNext()) {
                    Instr instr = iterator.next();
                    if (!activeInstrSet.contains(instr)) {
                        instr.RemoveAllValueUse();
                        iterator.remove();
                        finished = false;
                    }
                }
            }
        }

        return finished;
    }

    private HashSet<Instr> GetActiveInstrSet() {
        HashSet<Instr> activeInstrSet = new HashSet<>();
        Stack<Instr> todoInstrStack = new Stack<>();
        for (IrFunction irFunction : irModule.GetFunctions()) {
            for (IrBasicBlock irBasicBlock : irFunction.GetBasicBlocks()) {
                for (Instr instr : irBasicBlock.GetInstrList()) {
                    if (this.IsCriticalInstr(instr)) {
                        todoInstrStack.push(instr);
                    }
                }
            }
        }

        while (!todoInstrStack.isEmpty()) {
            Instr todoInstr = todoInstrStack.pop();
            activeInstrSet.add(todoInstr);
            for (IrValue useValue : todoInstr.GetUseValueList()) {
                if (useValue instanceof Instr useInstr && !activeInstrSet.contains(useInstr)) {
                    todoInstrStack.push(useInstr);
                    activeInstrSet.add(useInstr);
                }
            }
        }

        return activeInstrSet;
    }

    private boolean IsCriticalInstr(Instr instr) {
        return instr instanceof ReturnInstr ||
            (instr instanceof CallInstr callInstr &&
                this.sideEffectFunctions.contains(callInstr.GetTargetFunction())) ||
            instr instanceof BranchInstr || instr instanceof JumpInstr ||
            instr instanceof StoreInstr || instr instanceof IoInstr;
    }

    private boolean MergeBlock() {
        boolean finished = true;

        for (IrFunction irFunction : irModule.GetFunctions()) {
            for (IrBasicBlock irBasicBlock : irFunction.GetBasicBlocks()) {
                if (this.CanMergeBlock(irBasicBlock)) {
                    finished = false;
                    IrBasicBlock beforeBlock = irBasicBlock.GetBeforeBlocks().get(0);
                    beforeBlock.AppendBlock(irBasicBlock);
                }
            }
        }

        return finished;
    }

    private boolean CanMergeBlock(IrBasicBlock visitBlock) {
        ArrayList<IrBasicBlock> beforeBlockList = visitBlock.GetBeforeBlocks();
        if (beforeBlockList.size() == 1) {
            IrBasicBlock beforeBlock = beforeBlockList.get(0);
            // 前后对接上，则可以合并
            return beforeBlock.GetNextBlocks().size() == 1;
        }
        return false;
    }
}
