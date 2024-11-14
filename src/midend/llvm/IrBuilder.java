package midend.llvm;

import midend.llvm.constant.IrConstant;
import midend.llvm.instr.Instr;
import midend.llvm.type.IrArrayType;
import midend.llvm.type.IrBaseType;
import midend.llvm.type.IrType;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;
import midend.llvm.value.IrGlobalValue;
import midend.symbol.SymbolType;
import midend.symbol.ValueSymbol;

import java.util.HashMap;

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
    private static int globalVarNameCount = 0;
    private static HashMap<IrFunction, Integer> functionVarNameCountMap = new HashMap<>();

    public static void SetCurrentModule(IrModule irModule) {
        currentModule = irModule;
    }

    public static IrFunction GetNewFunctionIr(String name, IrType returnType) {
        IrFunction irFunction = new IrFunction(GetFuncName(name), returnType);
        currentModule.AddIrFunction(irFunction);
        // 设置为当前处理的Function
        currentFunction = irFunction;
        // 为Function添加一个基础basic block
        irFunction.AddBasicBlock(GetNewBasicBlockIr());

        // 添加计数表
        functionVarNameCountMap.put(irFunction, 0);

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

    public static String GetFuncName(String name) {
        return name.equals("main") ? "@" + name : FUNC_NAME_PREFIX + name;
    }

    public static String GetBasicBlockName() {
        return BasicBlock_NAME_PREFIX + basicBlockCount++;
    }

    public static String GetGlobalVarName() {
        return GLOBAL_VAR_NAME_PREFIX + globalVarNameCount++;
    }

    public static String GetFunctionVarName() {
        int count = functionVarNameCountMap.get(currentFunction);
        functionVarNameCountMap.put(currentFunction, count + 1);
        return LOCAL_VAR_NAME_PREFIX + count;
    }

    public static String GetParamName(String name) {
        return PARAM_NAME_PREFIX + name;
    }

    public static void AddInstr(Instr instr) {
        currentBasicBlock.AddInstr(instr);
    }
}
