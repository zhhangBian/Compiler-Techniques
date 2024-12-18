package backend;

import backend.mips.MipsBuilder;
import backend.mips.MipsModule;
import midend.MidEnd;
import midend.llvm.IrModule;
import utils.Setting;

public class BackEnd {
    private static IrModule midEndModule;
    private static MipsModule backEndModule;

    public static void GenerateMips() {
        backEndModule = new MipsModule();
        MipsBuilder.SetBackEndModule(backEndModule);

        midEndModule = MidEnd.GetIrModule();
        midEndModule.toMips();
        // 进行窥孔优化
        if (Setting.FINE_TUNING) {
            PeepHole peepHole = new PeepHole();
            peepHole.Peep();
        }
    }

    public static MipsModule GetMipsModule() {
        return backEndModule;
    }
}
