package backend.mips;

import backend.mips.assembly.MipsAssembly;
import backend.mips.assembly.data.MipsDataAssembly;
import midend.llvm.value.IrFunction;
import midend.llvm.value.IrValue;

import java.util.HashMap;

public class MipsBuilder {
    private static MipsModule currentModule = null;
    private static IrFunction currentFunction = null;
    // value-register分配表
    private static HashMap<IrFunction, HashMap<IrValue, Register>> functionRegisterMap = null;
    private static HashMap<IrValue, Register> currentValueMap = null;
    // 函数栈偏移量分配表
    private static int stackOffset = 0;
    private static HashMap<IrValue, Integer> stackOffsetMap = null;

    public static void SetBackEndModule(MipsModule mipsModule) {
        currentModule = mipsModule;
        functionRegisterMap = new HashMap<>();
    }

    public static void AddAssembly(MipsAssembly mipsAssembly) {
        if (mipsAssembly instanceof MipsDataAssembly) {
            currentModule.AddToData(mipsAssembly);
        } else {
            currentModule.AddToText(mipsAssembly);
        }
    }

    public static void SetCurrentFunction(IrFunction irFunction) {
        currentFunction = irFunction;
        // 设置相应的寄存器分配表
        HashMap<IrValue, Register> valueMap = new HashMap<>();
        functionRegisterMap.put(irFunction, valueMap);
        currentValueMap = valueMap;
        // 初始化栈分配表
        stackOffset = 0;
        stackOffsetMap = new HashMap<>();
    }
}
