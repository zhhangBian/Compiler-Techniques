package midend.llvm;

import midend.llvm.type.IrType;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;

public class IrBuilder {
    private static final String GLOBAL_VAR_NAME_PREFIX = "@g";
    private static final String STRING_LITERAL_NAME_PREFIX = "@s";
    private static final String LOCAL_VAR_NAME_PREFIX = "%v";
    private static final String PARAM_NAME_PREFIX = "%a";
    private static final String BasicBlock_NAME_PREFIX = "b";
    private static final String FUNC_NAME_PREFIX = "@f_";

    private static IrModule currentModule = null;
    private static IrBasicBlock currentBasicBlock = null;
    private static IrFunction currentFunction = null;

    private static int basicBlockCount = 0;

    public static void SetCurrentModule(IrModule irModule) {
        currentModule = irModule;
    }

    public static IrModule GetCurrentModule() {
        return currentModule;
    }

    public static void SetCurrentBasicBlock(IrBasicBlock basicBlock) {
        currentBasicBlock = basicBlock;
    }

    public static IrBasicBlock GetCurrentBasicBlock() {
        return currentBasicBlock;
    }

    public static void SetCurrentFunction(IrFunction function) {
        currentFunction = function;
    }

    public static IrFunction GetCurrentFunction() {
        return currentFunction;
    }

    public static IrFunction GetFunctionIr(String name, IrType returnType) {
        String funcName = name.equals("main") ? "@" + name : FUNC_NAME_PREFIX + name;
        return new IrFunction(funcName, returnType);
    }

    public static IrBasicBlock GetNewBasicBlock() {
        String irName = BasicBlock_NAME_PREFIX + (basicBlockCount++);
        IrBasicBlock basicBlock = new IrBasicBlock(irName);

        // 设置相应的函数关系
        currentFunction.AddBasicBlock(basicBlock);
        basicBlock.SetParentFunction(currentFunction);
        // 设置为当前解析的basicBlock
        currentBasicBlock = basicBlock;

        return basicBlock;
    }
}
