package midend.llvm;

import midend.llvm.constant.IrConstant;
import midend.llvm.constant.IrConstantString;
import midend.llvm.instr.Instr;
import midend.llvm.type.IrType;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;
import midend.llvm.value.IrGlobalValue;
import midend.llvm.value.IrLoop;

import java.util.HashMap;
import java.util.Stack;

public class IrBuilder {
    private static final String GLOBAL_VAR_NAME_PREFIX = "@g_";
    private static final String STRING_LITERAL_NAME_PREFIX = "@s_";
    private static final String LOCAL_VAR_NAME_PREFIX = "%v";
    private static final String BasicBlock_NAME_PREFIX = "b_";
    private static final String FUNC_NAME_PREFIX = "@f_";

    private static IrModule currentModule = null;
    private static IrBasicBlock currentBasicBlock = null;
    private static IrFunction currentFunction = null;
    private static final Stack<IrLoop> loopStack = new Stack<>();

    private static int basicBlockCount = 0;
    private static int globalVarNameCount = 0;
    private static int stringConstNameCount = 0;
    private static final HashMap<IrFunction, Integer> localVarNameCountMap = new HashMap<>();

    public static void SetCurrentModule(IrModule irModule) {
        currentModule = irModule;
    }

    public static IrModule GetCurrentModule() {
        return currentModule;
    }

    public static void Check() {
        currentModule.Check();
    }

    public static IrFunction GetNewFunctionIr(String name, IrType returnType) {
        // 创建新Function
        IrFunction irFunction = new IrFunction(GetFuncName(name), returnType);
        currentModule.AddIrFunction(irFunction);
        // 设置为当前处理的Function
        currentFunction = irFunction;
        // 为Function添加一个基础basic block
        IrBasicBlock irBasicBlock = GetNewBasicBlockIr();
        // 设置当前的basic block
        currentBasicBlock = irBasicBlock;

        // 添加计数表
        localVarNameCountMap.put(irFunction, 0);

        return irFunction;
    }

    public static IrBasicBlock GetNewBasicBlockIr() {
        IrBasicBlock basicBlock = new IrBasicBlock(GetBasicBlockName(), currentFunction);
        // 添加到当前的处理中
        currentFunction.AddBasicBlock(basicBlock);

        return basicBlock;
    }

    public static IrBasicBlock GetNewBasicBlockIr(IrFunction irFunction, IrBasicBlock afterBlock) {
        IrBasicBlock basicBlock = new IrBasicBlock(GetBasicBlockName(), irFunction);
        // 添加到当前的处理中
        irFunction.AddBasicBlock(basicBlock, afterBlock);

        return basicBlock;
    }

    public static void SetCurrentBasicBlock(IrBasicBlock irBasicBlock) {
        currentBasicBlock = irBasicBlock;
    }

    public static IrGlobalValue GetNewIrGlobalValue(IrType valueType, IrConstant initValue) {
        IrGlobalValue globalValue = new IrGlobalValue(valueType, GetGlobalVarName(), initValue);
        currentModule.AddIrGlobalValue(globalValue);

        return globalValue;
    }

    public static IrConstantString GetNewIrConstantString(String string) {
        return currentModule.GetNewIrConstantString(string);
    }

    public static String GetFuncName(String name) {
        return name.equals("main") ? "@" + name : FUNC_NAME_PREFIX + name;
    }

    public static String GetBasicBlockName() {
        return BasicBlock_NAME_PREFIX + basicBlockCount++;
    }

    public static String GetGlobalVarName() {
        return GLOBAL_VAR_NAME_PREFIX + globalVarNameCount++;
    }

    public static String GetLocalVarName() {
        int count = localVarNameCountMap.get(currentFunction);
        localVarNameCountMap.put(currentFunction, count + 1);
        return LOCAL_VAR_NAME_PREFIX + count;
    }

    public static String GetLocalVarName(IrFunction irFunction) {
        int count = localVarNameCountMap.get(irFunction);
        localVarNameCountMap.put(irFunction, count + 1);
        return LOCAL_VAR_NAME_PREFIX + count;
    }

    public static String GetStringConstName() {
        return STRING_LITERAL_NAME_PREFIX + stringConstNameCount++;
    }

    public static void AddInstr(Instr instr) {
        currentBasicBlock.AddInstr(instr);
        instr.SetInBasicBlock(currentBasicBlock);
    }

    public static IrBasicBlock GetCurrentBasicBlock() {
        return currentBasicBlock;
    }

    public static IrType GetCurrentFunctionReturnType() {
        return currentFunction.GetReturnType();
    }

    public static void LoopStackPush(IrLoop loop) {
        loopStack.push(loop);
    }

    public static void LoopStackPop() {
        loopStack.pop();
    }

    public static IrLoop LoopStackPeek() {
        return loopStack.peek();
    }
}
