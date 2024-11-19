package backend;

import midend.llvm.IrBuilder;
import midend.llvm.IrModule;

public class BackEnd {
    private static IrModule irModule;

    public static void SetIr() {
        irModule = IrBuilder.GetCurrentModule();

    }
}
