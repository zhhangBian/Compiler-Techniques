package midend.llvm;

import midend.llvm.value.IrFunction;
import midend.llvm.value.IrGlobalValue;

import java.util.ArrayList;

public class IrModule extends IrNode {
    // 全局变量
    private final ArrayList<IrGlobalValue> globalValues;
    // 一切运行对象皆为function，包括main
    private final ArrayList<IrFunction> functions;

    public IrModule() {
        this.globalValues = new ArrayList<>();
        this.functions = new ArrayList<>();
    }

    public void AddIrGlobalValue(IrGlobalValue globalValue) {
        this.globalValues.add(globalValue);
    }

    public void AddIrFunction(IrFunction function) {
        this.functions.add(function);
    }

    public ArrayList<IrGlobalValue> GetIrGlobalValues() {
        return this.globalValues;
    }

    public ArrayList<IrFunction> GetIrFunctions() {
        return this.functions;
    }

    @Override
    public String toString() {
        throw new RuntimeException("not finished yet");
    }
}
