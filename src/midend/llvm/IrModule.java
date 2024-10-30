package midend.llvm;

import midend.llvm.value.IrFunction;
import midend.llvm.value.IrGlobalVariable;

import java.util.ArrayList;

public class IrModule extends IrNode {
    // 全局变量
    private static final ArrayList<IrGlobalVariable> globalVariables = new ArrayList<>();
    // 一切运行对象皆为function，包括main
    private static final ArrayList<IrFunction> functions = new ArrayList<>();

    public static void AddIrGlobalVariable(IrGlobalVariable globalVariable) {
        globalVariables.add(globalVariable);
    }

    public static void AddIrFunction(IrFunction function) {
        functions.add(function);
    }

    public static ArrayList<IrGlobalVariable> GetIrGlobalVariables() {
        return globalVariables;
    }

    public static ArrayList<IrFunction> GetIrFunctions() {
        return functions;
    }

    @Override
    public String toString() {
        throw new RuntimeException("not finished yet");
    }
}
