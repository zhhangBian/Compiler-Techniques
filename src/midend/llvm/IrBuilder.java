package midend.llvm;

import midend.llvm.constant.IrConstant;
import midend.llvm.constant.IrConstantString;
import midend.llvm.instr.Instr;
import midend.llvm.type.IrType;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;
import midend.llvm.value.IrGlobalValue;

import java.util.HashMap;

public class IrBuilder {
    private static final String GLOBAL_VAR_NAME_PREFIX = "@g_";
    private static final String STRING_LITERAL_NAME_PREFIX = "@s_";
    private static final String LOCAL_VAR_NAME_PREFIX = "%v";
    private static final String PARAM_NAME_PREFIX = "%a_";
    private static final String BasicBlock_NAME_PREFIX = "b_";
    private static final String FUNC_NAME_PREFIX = "@f_";

    private static IrModule currentModule = null;
    private static IrBasicBlock currentBasicBlock = null;
    private static IrFunction currentFunction = null;

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

    public static IrFunction GetNewFunctionIr(String name, IrType returnType) {
        IrFunction irFunction = new IrFunction(GetFuncName(name), returnType);
        currentModule.AddIrFunction(irFunction);
        // 设置为当前处理的Function
        currentFunction = irFunction;
        // 为Function添加一个基础basic block
        irFunction.AddBasicBlock(GetNewBasicBlockIr());

        // 添加计数表
        localVarNameCountMap.put(irFunction, 0);

        return irFunction;
    }

    public static IrBasicBlock GetNewBasicBlockIr() {
        String irName = GetBasicBlockName();
        IrBasicBlock basicBlock = new IrBasicBlock(irName);
        // 设置从属关系
        basicBlock.SetParentFunction(currentFunction);
        // 设置为当前解析的basicBlock
        currentBasicBlock = basicBlock;

        return basicBlock;
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

    public static String GetStringConstName() {
        return STRING_LITERAL_NAME_PREFIX + stringConstNameCount++;
    }

    public static String GetParamName(String name) {
        return PARAM_NAME_PREFIX + name;
    }

    public static void AddInstr(Instr instr) {
        currentBasicBlock.AddInstr(instr);
    }

    public static IrBasicBlock GetCurrentBasicBlock() {
        return currentBasicBlock;
    }

    public static IrType GetCurrentFunctionReturnType() {
        return currentFunction.GetReturnType();
    }
}
