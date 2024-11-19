package backend.mips;

import backend.mips.assembly.MipsAssembly;
import midend.llvm.IrModule;

public class MipsBuilder {
    private static IrModule midEndModule = null;
    private static MipsModule currentModule = null;

    public static void SetMidEndModule(IrModule irModule) {
        midEndModule = irModule;
    }

    public static void SetBackEndModule(MipsModule mipsModule) {
        currentModule = mipsModule;
    }

    public static void AddToData(MipsAssembly mipsAssembly) {
        currentModule.AddToData(mipsAssembly);
    }

    public static void AddToText(MipsAssembly mipsAssembly) {
        currentModule.AddToText(mipsAssembly);
    }
}
