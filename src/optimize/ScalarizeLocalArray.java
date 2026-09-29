package optimize;

import midend.llvm.IrBuilder;
import midend.llvm.constant.IrConstant;
import midend.llvm.instr.AllocateInstr;
import midend.llvm.instr.GepInstr;
import midend.llvm.instr.Instr;
import midend.llvm.instr.LoadInstr;
import midend.llvm.instr.StoreInstr;
import midend.llvm.type.IrArrayType;
import midend.llvm.use.IrUse;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;

import java.util.ArrayList;
import java.util.TreeMap;

/** 将不逃逸且仅按常量下标访问的小数组交给现有 MemToReg 处理。 */
public class ScalarizeLocalArray extends Optimizer {
    private static final int MAX_ELEMENTS = 16;

    @Override
    public void Optimize() {
        for (IrFunction function : irModule.GetFunctions()) {
            for (IrBasicBlock block : function.GetBasicBlocks()) {
                for (Instr instr : new ArrayList<>(block.GetInstrList())) {
                    if (instr instanceof AllocateInstr allocate &&
                        allocate.GetTargetType() instanceof IrArrayType array &&
                        array.GetArraySize() <= MAX_ELEMENTS && array.GetArraySize() > 0 &&
                        !(array.GetElementType() instanceof IrArrayType) &&
                        this.CanScalarize(allocate, array)) {
                        this.Scalarize(function, block, allocate, array);
                    }
                }
            }
        }
    }

    private boolean CanScalarize(AllocateInstr allocate, IrArrayType array) {
        for (IrUse use : allocate.GetUseList()) {
            if (!(use.GetUser() instanceof GepInstr gep) || gep.GetPointer() != allocate ||
                !(gep.GetOffset() instanceof IrConstant constant)) {
                return false;
            }
            int index = Integer.parseInt(constant.GetIrName());
            if (index < 0 || index >= array.GetArraySize()) {
                return false;
            }
            for (IrUse gepUse : gep.GetUseList()) {
                if (!(gepUse.GetUser() instanceof LoadInstr load &&
                    load.GetPointer() == gep) &&
                    !(gepUse.GetUser() instanceof StoreInstr store &&
                        store.GetAddressValue() == gep)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void Scalarize(IrFunction function, IrBasicBlock block,
                           AllocateInstr allocate, IrArrayType array) {
        TreeMap<Integer, AllocateInstr> elements = new TreeMap<>();
        IrBasicBlock staging = new IrBasicBlock("scalar_stage", function);
        IrBuilder.BuildInContext(function, staging, () -> {
            for (IrUse use : new ArrayList<>(allocate.GetUseList())) {
                GepInstr gep = (GepInstr) use.GetUser();
                int index = Integer.parseInt(gep.GetOffset().GetIrName());
                elements.computeIfAbsent(index,
                    ignored -> new AllocateInstr(array.GetElementType()));
            }
        });

        int position = block.GetInstrList().indexOf(allocate);
        block.GetInstrList().addAll(position, staging.GetInstrList());
        for (Instr scalar : staging.GetInstrList()) {
            scalar.SetInBasicBlock(block);
        }
        for (IrUse use : new ArrayList<>(allocate.GetUseList())) {
            GepInstr gep = (GepInstr) use.GetUser();
            int index = Integer.parseInt(gep.GetOffset().GetIrName());
            gep.ModifyAllUsersToNewValue(elements.get(index));
            gep.RemoveAllValueUse();
            gep.GetInBasicBlock().GetInstrList().remove(gep);
        }
        allocate.RemoveAllValueUse();
        block.GetInstrList().remove(allocate);
    }
}
