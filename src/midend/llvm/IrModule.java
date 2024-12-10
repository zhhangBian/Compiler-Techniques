package midend.llvm;

import backend.mips.Register;
import backend.mips.assembly.MipsAnnotation;
import backend.mips.assembly.MipsJump;
import backend.mips.assembly.MipsLabel;
import backend.mips.assembly.MipsSyscall;
import backend.mips.assembly.fake.MarsLi;
import midend.llvm.constant.IrConstantString;
import midend.llvm.instr.io.GetCharInstr;
import midend.llvm.instr.io.GetIntInstr;
import midend.llvm.instr.io.PrintCharInstr;
import midend.llvm.instr.io.PrintIntInstr;
import midend.llvm.instr.io.PrintStrInstr;
import midend.llvm.value.IrFunction;
import midend.llvm.value.IrGlobalValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IrModule extends IrNode {
    // 函数声明
    private final ArrayList<String> declares;
    // 字符串
    private final HashMap<String, IrConstantString> stringConstantMap;
    // 全局变量
    private final ArrayList<IrGlobalValue> globalValues;
    // 一切运行对象皆为function，包括main
    private final ArrayList<IrFunction> functions;

    public IrModule() {
        this.declares = new ArrayList<>();
        this.stringConstantMap = new HashMap<>();
        this.globalValues = new ArrayList<>();
        this.functions = new ArrayList<>();

        this.declares.add(GetCharInstr.GetDeclare());
        this.declares.add(GetIntInstr.GetDeclare());
        this.declares.add(PrintCharInstr.GetDeclare());
        this.declares.add(PrintIntInstr.GetDeclare());
        this.declares.add(PrintStrInstr.GetDeclare());
    }

    public void Check() {
        for (IrFunction irFunction : this.functions) {
            irFunction.CheckNoEmptyBlock();
        }
    }

    public IrConstantString GetNewIrConstantString(String string) {
        if (this.stringConstantMap.containsKey(string)) {
            return this.stringConstantMap.get(string);
        } else {
            IrConstantString irConstantString =
                new IrConstantString(IrBuilder.GetStringConstName(), string);
            this.stringConstantMap.put(string, irConstantString);
            return irConstantString;
        }
    }

    public void AddIrGlobalValue(IrGlobalValue globalValue) {
        this.globalValues.add(globalValue);
    }

    public void AddIrFunction(IrFunction function) {
        this.functions.add(function);
    }

    public ArrayList<IrFunction> GetFunctions() {
        return this.functions;
    }

    public IrFunction GetMainFunction() {
        for (IrFunction irFunction : this.functions) {
            if (irFunction.IsMainFunction()) {
                return irFunction;
            }
        }
        throw new RuntimeException("no main function");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (String declare : this.declares) {
            builder.append(declare);
            builder.append("\n");
        }
        builder.append("\n");

        List<Map.Entry<String, IrConstantString>> entryList =
            new ArrayList<>(this.stringConstantMap.entrySet());
        entryList.sort((o1, o2) ->
            CharSequence.compare(o1.getValue().GetIrName(), o2.getValue().GetIrName()));
        for (Map.Entry<String, IrConstantString> entry : entryList) {
            builder.append(entry.getValue());
            builder.append("\n");
        }
        builder.append("\n");

        for (IrGlobalValue globalValue : this.globalValues) {
            builder.append(globalValue);
            builder.append("\n");
        }
        builder.append("\n");

        for (IrFunction irFunction : this.functions) {
            builder.append(irFunction);
            builder.append("\n\n");
        }

        return builder.toString();
    }

    public void toMips() {
        for (IrGlobalValue globalValue : this.globalValues) {
            globalValue.toMips();
        }
        // 字符串放到之后，避免对齐问题
        for (Map.Entry<String, IrConstantString> entry : this.stringConstantMap.entrySet()) {
            entry.getValue().toMips();
        }

        // 插入跳转到main函数
        new MipsAnnotation("jump to main");
        new MipsJump(MipsJump.JumpType.JAL, "main");
        new MipsJump(MipsJump.JumpType.J, "end");

        for (IrFunction irFunction : this.functions) {
            irFunction.toMips();
        }

        // 设置标签
        new MipsLabel("end");
        // 设置syscall
        new MarsLi(Register.V0, 10);
        new MipsSyscall();
    }
}
