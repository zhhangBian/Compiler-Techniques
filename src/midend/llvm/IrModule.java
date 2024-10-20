package midend.llvm;

import frontend.ast.Node;
import midend.llvm.value.function.IrFunction;
import midend.llvm.value.variable.IrGlobalVariable;

import java.util.ArrayList;

public class IrModule extends IrNode {
    private static Node rootNode;
    // 全局变量
    private static final ArrayList<IrGlobalVariable> globalVariables = new ArrayList<>();
    // 一切运行对象皆为function，包括main
    private static final ArrayList<IrFunction> functions = new ArrayList<>();

    public static void SetRootNode(Node node) {
        rootNode = node;
    }

    public static void GenerateIrModule() {
        rootNode.CreateSymbol();
    }

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
        throw new RuntimeException();
    }
}
