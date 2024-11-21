package backend;

import backend.mips.MipsBuilder;
import backend.mips.MipsModule;
import midend.MidEnd;
import midend.llvm.IrModule;

public class BackEnd {
    private static IrModule midEndModule;
    private static MipsModule backEndModule;

    public static void GenerateMips() {
        backEndModule = new MipsModule();
        MipsBuilder.SetBackEndModule(backEndModule);

        midEndModule = MidEnd.GetIrModule();
        midEndModule.toMips();
    }

    public static MipsModule GetMipsModule() {
        return backEndModule;
    }
}
