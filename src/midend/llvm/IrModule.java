package midend.llvm;

import midend.llvm.value.IrFunction;
import midend.llvm.value.IrGlobalVariable;

import java.util.ArrayList;

public class IrModule extends IrNode {
    // 全局变量
    private final ArrayList<IrGlobalVariable> globalVariables;
    // 一切运行对象皆为function，包括main
    private final ArrayList<IrFunction> functions;

    public IrModule() {
        this.globalVariables = new ArrayList<>();
        this.functions = new ArrayList<>();
    }

    public void AddIrGlobalVariable(IrGlobalVariable globalVariable) {
        this.globalVariables.add(globalVariable);
    }

    public void AddIrFunction(IrFunction function) {
        this.functions.add(function);
    }

    public ArrayList<IrGlobalVariable> GetIrGlobalVariables() {
        return this.globalVariables;
    }

    public ArrayList<IrFunction> GetIrFunctions() {
        return this.functions;
    }

    @Override
    public String toString() {
        throw new RuntimeException("not finished yet");
    }
}
