package optimize;

import backend.mips.Register;
import midend.llvm.value.IrValue;

import java.util.HashMap;

public class AllocateRegister extends Optimizer {
    private HashMap<Register, IrValue> registerValueMap;
    private HashMap<IrValue, Register> valueRegisterMap;

    public AllocateRegister() {
        this.EmptyMap();
    }

    private void EmptyMap() {
        this.registerValueMap = new HashMap<>();
        this.valueRegisterMap = new HashMap<>();
    }

    @Override
    public void Optimize() {

    }
}
