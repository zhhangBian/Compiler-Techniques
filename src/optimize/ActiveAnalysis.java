package optimize;

import midend.llvm.instr.Instr;
import midend.llvm.instr.phi.PhiInstr;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;
import midend.llvm.value.IrGlobalValue;
import midend.llvm.value.IrParameter;
import midend.llvm.value.IrValue;
import utils.Debug;

import java.util.ArrayList;
import java.util.HashSet;

public class ActiveAnalysis extends Optimizer {
    @Override
    public void Optimize() {
        // 清除原先的活跃分析
        this.ClearActiveInfo();
        // 分析def和use
        this.AnalysisDefAndUse();
        // 分析in和out
        this.AnalysisInAndOut();
    }

    private void ClearActiveInfo() {
        for (IrFunction irFunction : irModule.GetFunctions()) {
            for (IrBasicBlock irBasicBlock : irFunction.GetBasicBlocks()) {
                irBasicBlock.ClearActiveInfo();
            }
        }
    }

    private void AnalysisDefAndUse() {
        for (IrFunction irFunction : irModule.GetFunctions()) {
            for (IrBasicBlock irBasicBlock : irFunction.GetBasicBlocks()) {
                HashSet<IrValue> defSet = irBasicBlock.GetDefValueSet();
                HashSet<IrValue> useSet = irBasicBlock.GetUseValueSet();

                // 先分析phi指令：由前序时间传入，最早发生
                for (Instr instr : irBasicBlock.GetInstrList()) {
                    if (instr instanceof PhiInstr phiInstr) {
                        for (IrValue useValue : phiInstr.GetUseValueList()) {
                            if (this.IsUseValue(useValue)) {
                                useSet.add(useValue);
                            }
                        }
                    }
                }
                // 分析其他指令
                for (Instr instr : irBasicBlock.GetInstrList()) {
                    // 对instr使用的数据分析
                    for (IrValue useValue : instr.GetUseValueList()) {
                        if (!defSet.contains(useValue) && this.IsUseValue(useValue)) {
                            useSet.add(useValue);
                        }
                    }
                    // 对instr本身
                    if (!useSet.contains(instr) || instr.DefValue()) {
                        defSet.add(instr);
                    }
                }
            }
        }
    }

    private boolean IsUseValue(IrValue useValue) {
        return useValue instanceof Instr ||
            useValue instanceof IrParameter ||
            useValue instanceof IrGlobalValue;
    }

    // in[B] = use[B] \cup (out[B] - def[B])
    // out[B] = \cup in[P] P为B的后继基本块
    private void AnalysisInAndOut() {
        for (IrFunction irFunction : irModule.GetFunctions()) {
            ArrayList<IrBasicBlock> blockList = irFunction.GetBasicBlocks();
            // 进行分析，直到不发生改变
            boolean haveChange = true;
            while (haveChange) {
                haveChange = false;
                // 对block进行逆序分析

                for (int i = blockList.size() - 1; i >= 0; i--) {
                    IrBasicBlock analysisBlock = blockList.get(i);

                    // out：对于后继块
                    HashSet<IrValue> newOutValueSet = new HashSet<>();
                    for (IrBasicBlock nextBlock : analysisBlock.GetNextBlocks()) {
                        newOutValueSet.addAll(nextBlock.GetInValueSet());
                    }
                    // in：
                    HashSet<IrValue> newInValueSet = new HashSet<>();
                    newInValueSet.addAll(newOutValueSet);
                    newInValueSet.removeAll(analysisBlock.GetDefValueSet());
                    newInValueSet.addAll(analysisBlock.GetUseValueSet());

                    HashSet<IrValue> oldInValueSet = analysisBlock.GetInValueSet();
                    HashSet<IrValue> oldOutValueSet = analysisBlock.GetOutValueSet();
                    if (!newOutValueSet.equals(oldOutValueSet) ||
                        !newInValueSet.equals(oldInValueSet)) {
                        haveChange = true;
                        analysisBlock.SetInValueSet(newInValueSet);
                        analysisBlock.SetOutValueSet(newOutValueSet);
                    }
                }
            }
        }
    }

    private void DebugPrint() {
        for (IrFunction irFunction : irModule.GetFunctions()) {
            Debug.DebugPrint(irFunction.GetIrName() + ":");
            for (IrBasicBlock irBasicBlock : irFunction.GetBasicBlocks()) {
                Debug.DebugPrint(irBasicBlock.GetIrName() + ":");
                Debug.DebugPrint("\tdef:" + irBasicBlock.GetDefValueSet());
                Debug.DebugPrint("\tuse:" + irBasicBlock.GetUseValueSet());
                Debug.DebugPrint("\tin :" + irBasicBlock.GetInValueSet());
                Debug.DebugPrint("\tout:" + irBasicBlock.GetOutValueSet());
            }
        }
    }
}
