package backend.mips;

import backend.mips.assembly.MipsAssembly;
import backend.mips.assembly.data.MipsDataAssembly;
import midend.llvm.value.IrFunction;
import midend.llvm.value.IrParameter;
import midend.llvm.value.IrValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MipsBuilder {
    private static MipsModule currentModule = null;
    // value-register分配表
    private static HashMap<IrValue, Register> valueRegisterMap = null;
    // 函数栈偏移量分配表
    private static int stackOffset = 0;
    private static HashMap<IrValue, Integer> stackOffsetValueMap = null;

    public static void SetBackEndModule(MipsModule mipsModule) {
        currentModule = mipsModule;
    }

    public static void AddAssembly(MipsAssembly mipsAssembly) {
        if (mipsAssembly instanceof MipsDataAssembly) {
            currentModule.AddToData(mipsAssembly);
        } else {
            currentModule.AddToText(mipsAssembly);
        }
    }

    public static void SetCurrentFunction(IrFunction irFunction) {
        // 设置相应的寄存器分配表
        valueRegisterMap = irFunction.GetValueRegisterMap();
        // 初始化栈分配表
        stackOffset = 0;
        stackOffsetValueMap = new HashMap<>();
    }

    public static Register GetValueToRegister(IrValue irValue) {
        return valueRegisterMap.get(irValue);
    }

    public static void AllocateRegForParam(IrParameter irParameter, Register register) {
        valueRegisterMap.put(irParameter, register);
    }

    public static ArrayList<Register> GetAllocatedRegList() {
        return new ArrayList<>(new HashSet<>(valueRegisterMap.values()));
    }

    public static int GetCurrentStackOffset() {
        return stackOffset;
    }

    public static Integer GetStackValueOffset(IrValue irValue) {
        return stackOffsetValueMap.get(irValue);
    }

    public static Integer AllocateStackForValue(IrValue irValue) {
        Integer address = stackOffsetValueMap.get(irValue);
        if (address == null) {
            stackOffset -= 4;
            stackOffsetValueMap.put(irValue, stackOffset);
            address = stackOffset;
        }

        return address;
    }

    public static void AllocateStackSpace(int offset) {
        stackOffset -= offset;
    }
}
