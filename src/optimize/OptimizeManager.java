package optimize;

import midend.MidEnd;
import utils.IOhandler;
import utils.Setting;

import java.io.IOException;
import java.util.ArrayList;

public class OptimizeManager {
    private static ArrayList<Optimizer> optimizerList;

    public static void Init() throws IOException {
        Optimizer.SetIrModule(MidEnd.GetIrModule());

        optimizerList = new ArrayList<>();
        // 这里的顺序是关键的
        optimizerList.add(new RemoveUnReachCode());
        optimizerList.add(new CfgBuilder());
        optimizerList.add(new RemoveDeadBlock());
        optimizerList.add(new CfgBuilder());

        optimizerList.add(new MemToReg());
        optimizerList.add(new CfgBuilder());

//        for (Optimizer optimizer : optimizerList) {
//            optimizer.Optimize();
//        }
//        optimizerList.clear();

        // 死代码删除
        optimizerList.add(new RemoveUnReachCode());
        optimizerList.add(new RemoveDeadCode());
        optimizerList.add(new CfgBuilder());

        try {
            IOhandler.PrintLlvmPhi();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        optimizerList.add(new ActiveAnalysis());
        if (!Setting.SPECIAL) {
            optimizerList.add(new AllocateRegister());
        }
        optimizerList.add(new RemovePhi());
    }

    public static void Optimize() {
        for (Optimizer optimizer : optimizerList) {
            optimizer.Optimize();
        }
    }
}
