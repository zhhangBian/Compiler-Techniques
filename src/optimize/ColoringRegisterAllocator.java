package optimize;

import backend.mips.Register;
import midend.llvm.constant.IrConstant;
import midend.llvm.instr.CallInstr;
import midend.llvm.instr.GepInstr;
import midend.llvm.instr.Instr;
import midend.llvm.instr.phi.PhiInstr;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;
import midend.llvm.value.IrValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class ColoringRegisterAllocator extends Optimizer {
    private static class LiveInfo {
        private final HashSet<IrValue> def = new HashSet<>();
        private final HashSet<IrValue> use = new HashSet<>();
        private HashSet<IrValue> in = new HashSet<>();
        private HashSet<IrValue> out = new HashSet<>();
    }

    @Override
    public void Optimize() {
        for (IrFunction irFunction : irModule.GetFunctions()) {
            this.Allocate(irFunction);
        }
    }

    private void Allocate(IrFunction irFunction) {
        LinkedHashMap<IrValue, HashSet<IrValue>> graph = new LinkedHashMap<>();
        for (IrBasicBlock block : irFunction.GetBasicBlocks()) {
            for (Instr instr : block.GetInstrList()) {
                if (instr.DefValue()) {
                    graph.put(instr, new HashSet<>());
                }
            }
        }

        HashSet<IrValue> tracked = new HashSet<>(graph.keySet());
        tracked.addAll(irFunction.GetParameterList());
        HashMap<IrBasicBlock, LiveInfo> liveInfo = this.AnalyzeLiveness(irFunction, tracked);
        this.BuildInterferenceGraph(irFunction, graph, tracked, liveInfo);
        this.ColorGraph(graph, this.BuildCopyPreferences(irFunction, graph),
            irFunction.GetValueRegisterMap());
    }

    private HashMap<IrBasicBlock, LiveInfo> AnalyzeLiveness(
        IrFunction irFunction, HashSet<IrValue> tracked) {
        HashMap<IrBasicBlock, LiveInfo> liveInfo = new HashMap<>();
        for (IrBasicBlock block : irFunction.GetBasicBlocks()) {
            LiveInfo info = new LiveInfo();
            liveInfo.put(block, info);
            for (Instr instr : block.GetInstrList()) {
                // phi的输入在前驱边上使用，不在当前块内使用
                if (!(instr instanceof PhiInstr)) {
                    for (IrValue value : instr.GetUseValueList()) {
                        if (tracked.contains(value) && !info.def.contains(value)) {
                            info.use.add(value);
                        }
                    }
                }
                if (tracked.contains(instr)) {
                    info.def.add(instr);
                }
            }
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            ArrayList<IrBasicBlock> blocks = irFunction.GetBasicBlocks();
            for (int i = blocks.size() - 1; i >= 0; i--) {
                IrBasicBlock block = blocks.get(i);
                LiveInfo info = liveInfo.get(block);
                HashSet<IrValue> newOut = new HashSet<>();
                for (IrBasicBlock nextBlock : block.GetNextBlocks()) {
                    newOut.addAll(liveInfo.get(nextBlock).in);
                    for (Instr instr : nextBlock.GetInstrList()) {
                        if (!(instr instanceof PhiInstr phiInstr)) {
                            break;
                        }
                        int index = phiInstr.GetBeforeBlockList().indexOf(block);
                        if (index >= 0) {
                            IrValue value = phiInstr.GetUseValueList().get(index);
                            if (tracked.contains(value)) {
                                newOut.add(value);
                            }
                        }
                    }
                }
                HashSet<IrValue> newIn = new HashSet<>(newOut);
                newIn.removeAll(info.def);
                newIn.addAll(info.use);
                if (!newIn.equals(info.in) || !newOut.equals(info.out)) {
                    info.in = newIn;
                    info.out = newOut;
                    changed = true;
                }
            }
        }
        return liveInfo;
    }

    private void BuildInterferenceGraph(IrFunction irFunction,
                                        LinkedHashMap<IrValue, HashSet<IrValue>> graph,
                                        HashSet<IrValue> tracked,
                                        HashMap<IrBasicBlock, LiveInfo> liveInfo) {
        for (IrBasicBlock block : irFunction.GetBasicBlocks()) {
            HashSet<IrValue> live = new HashSet<>(liveInfo.get(block).out);
            ArrayList<Instr> instrList = block.GetInstrList();
            for (int i = instrList.size() - 1; i >= 0; i--) {
                Instr instr = instrList.get(i);
                if (instr instanceof CallInstr call) {
                    HashSet<IrValue> liveAcross = new HashSet<>(live);
                    liveAcross.remove(call);
                    call.SetLiveAcross(liveAcross);
                }
                if (graph.containsKey(instr)) {
                    for (IrValue value : live) {
                        if (graph.containsKey(value)) {
                            this.AddEdge(graph, instr, value);
                        }
                    }
                    // 动态 GEP 在读取基址前会先写结果寄存器，二者不能共用。
                    if (instr instanceof GepInstr gep && !(gep.GetOffset() instanceof IrConstant)
                        && graph.containsKey(gep.GetPointer())) {
                        this.AddEdge(graph, instr, gep.GetPointer());
                    }
                    live.remove(instr);
                }
                if (!(instr instanceof PhiInstr)) {
                    for (IrValue value : instr.GetUseValueList()) {
                        if (tracked.contains(value)) {
                            live.add(value);
                        }
                    }
                }
            }
        }
    }

    private LinkedHashMap<IrValue, ArrayList<IrValue>> BuildCopyPreferences(
        IrFunction irFunction, LinkedHashMap<IrValue, HashSet<IrValue>> graph) {
        LinkedHashMap<IrValue, ArrayList<IrValue>> preferences = new LinkedHashMap<>();
        for (IrBasicBlock block : irFunction.GetBasicBlocks()) {
            for (Instr instr : block.GetInstrList()) {
                if (!(instr instanceof PhiInstr phi)) {
                    break;
                }
                for (IrValue source : phi.GetUseValueList()) {
                    if (graph.containsKey(source) && !graph.get(phi).contains(source)) {
                        preferences.computeIfAbsent(phi, ignored -> new ArrayList<>()).add(source);
                        preferences.computeIfAbsent(source, ignored -> new ArrayList<>()).add(phi);
                    }
                }
            }
        }
        return preferences;
    }

    private void AddEdge(LinkedHashMap<IrValue, HashSet<IrValue>> graph,
                         IrValue valueA, IrValue valueB) {
        if (valueA != valueB) {
            graph.get(valueA).add(valueB);
            graph.get(valueB).add(valueA);
        }
    }

    private void ColorGraph(LinkedHashMap<IrValue, HashSet<IrValue>> graph,
                            LinkedHashMap<IrValue, ArrayList<IrValue>> preferences,
                            HashMap<IrValue, Register> registerMap) {
        ArrayList<Register> registers = Register.GetUsAbleRegisters();
        LinkedHashMap<IrValue, HashSet<IrValue>> remaining = new LinkedHashMap<>();
        for (IrValue value : graph.keySet()) {
            remaining.put(value, new HashSet<>(graph.get(value)));
        }

        ArrayList<IrValue> selectStack = new ArrayList<>();
        while (!remaining.isEmpty()) {
            IrValue selected = null;
            int largestDegree = -1;
            for (IrValue value : remaining.keySet()) {
                int degree = remaining.get(value).size();
                if (degree < registers.size()) {
                    selected = value;
                    break;
                }
                if (degree > largestDegree) {
                    selected = value;
                    largestDegree = degree;
                }
            }
            selectStack.add(selected);
            remaining.remove(selected);
            for (IrValue neighbor : graph.get(selected)) {
                if (remaining.containsKey(neighbor)) {
                    remaining.get(neighbor).remove(selected);
                }
            }
        }

        registerMap.clear();
        for (int i = selectStack.size() - 1; i >= 0; i--) {
            IrValue value = selectStack.get(i);
            HashSet<Register> usedRegisters = new HashSet<>();
            for (IrValue neighbor : graph.get(value)) {
                if (registerMap.containsKey(neighbor)) {
                    usedRegisters.add(registerMap.get(neighbor));
                }
            }
            ArrayList<Register> preferred = new ArrayList<>();
            for (IrValue copyPartner : preferences.getOrDefault(value, new ArrayList<>())) {
                Register register = registerMap.get(copyPartner);
                if (register != null && !preferred.contains(register)) {
                    preferred.add(register);
                }
            }
            for (Register register : preferred) {
                if (!usedRegisters.contains(register)) {
                    registerMap.put(value, register);
                    break;
                }
            }
            if (registerMap.containsKey(value)) {
                continue;
            }
            for (Register register : registers) {
                if (!usedRegisters.contains(register)) {
                    registerMap.put(value, register);
                    break;
                }
            }
            // 无可用颜色的值保持未分配，现有后端会将其保存到栈上。
        }
    }
}
