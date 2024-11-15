package midend.llvm;

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
        builder.append("\n");

        return builder.toString();
    }
}
