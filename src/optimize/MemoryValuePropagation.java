package optimize;

import midend.llvm.instr.CallInstr;
import midend.llvm.instr.Instr;
import midend.llvm.instr.LoadInstr;
import midend.llvm.instr.StoreInstr;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;
import midend.llvm.value.IrValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/** 块内访存值传播，遇到可能写同一地址的操作就失效。 */
public class MemoryValuePropagation extends Optimizer {
    @Override
    public void Optimize() {
        for (IrFunction function : irModule.GetFunctions()) {
            for (IrBasicBlock block : function.GetBasicBlocks()) {
                this.OptimizeBlock(block);
            }
        }
    }

    private void OptimizeBlock(IrBasicBlock block) {
        HashMap<IrValue, IrValue> knownValues = new HashMap<>();
        HashMap<IrValue, StoreInstr> pendingStores = new HashMap<>();
        HashSet<StoreInstr> deadStores = new HashSet<>();
        Iterator<Instr> iterator = block.GetInstrList().iterator();
        while (iterator.hasNext()) {
            Instr instr = iterator.next();
            if (instr instanceof CallInstr) {
                knownValues.clear();
                pendingStores.clear();
            } else if (instr instanceof LoadInstr load) {
                IrValue pointer = load.GetPointer();
                for (IrValue address : new ArrayList<>(pendingStores.keySet())) {
                    if (MemoryAlias.MayAlias(address, pointer)) {
                        pendingStores.remove(address);
                    }
                }
                IrValue known = knownValues.get(pointer);
                if (known != null) {
                    load.ModifyAllUsersToNewValue(known);
                    load.RemoveAllValueUse();
                    iterator.remove();
                } else {
                    knownValues.put(pointer, load);
                }
            } else if (instr instanceof StoreInstr store) {
                IrValue pointer = store.GetAddressValue();
                for (IrValue address : new ArrayList<>(knownValues.keySet())) {
                    if (MemoryAlias.MayAlias(address, pointer)) {
                        knownValues.remove(address);
                    }
                }
                for (IrValue address : new ArrayList<>(pendingStores.keySet())) {
                    if (MemoryAlias.MayAlias(address, pointer)) {
                        if (address == pointer) {
                            deadStores.add(pendingStores.get(address));
                        }
                        pendingStores.remove(address);
                    }
                }
                knownValues.put(pointer, store.GetValueValue());
                pendingStores.put(pointer, store);
            }
        }
        for (StoreInstr store : deadStores) {
            store.RemoveAllValueUse();
        }
        block.GetInstrList().removeAll(deadStores);
    }
}
