package optimize;

import midend.MidEnd;
import utils.IOhandler;

import java.io.IOException;
import java.util.ArrayList;

public class OptimizeManager {
    private static ArrayList<Optimizer> optimizerList;

    public static void Init() {
        Optimizer.SetIrModule(MidEnd.GetIrModule());

        optimizerList = new ArrayList<>();
        // 这里的顺序是关键的
        optimizerList.add(new RemoveUselessCode());
        optimizerList.add(new CfgBuilder());
        optimizerList.add(new MemToReg());

        for (Optimizer optimizer : optimizerList) {
            optimizer.Optimize();
        }
        try {
            IOhandler.PrintLlvmPhi();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        optimizerList.clear();

        optimizerList.add(new AllocateRegister());
        optimizerList.add(new RemovePhi());

        for (Optimizer optimizer : optimizerList) {
            optimizer.Optimize();
        }
    }

    public static void Optimize() {
        for (Optimizer optimizer : optimizerList) {
            //optimizer.Optimize();
        }
    }
}
