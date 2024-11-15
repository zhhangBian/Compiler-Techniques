package midend.llvm.instr;

import midend.llvm.type.IrBaseType;
import midend.llvm.type.IrPointerType;
import midend.llvm.value.IrValue;

public class GepInstr extends Instr {
    public GepInstr(String irName, IrValue pointer, IrValue offest) {
        super(new IrPointerType(IrBaseType.INT32), irName, InstrType.GEP);
        this.AddUseValue(pointer);
        this.AddUseValue(offest);
    }

    public IrValue GetPointer() {
        return this.useValueList.get(0);
    }

    public IrValue GetOffset() {
        return this.useValueList.get(1);
    }
}
