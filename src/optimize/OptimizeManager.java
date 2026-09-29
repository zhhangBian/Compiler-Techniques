package optimize;

import midend.MidEnd;
import utils.Setting;

import java.util.ArrayList;

public class OptimizeManager {
    private static ArrayList<Optimizer> optimizerList;

    public static void Init() {
        Optimizer.SetIrModule(MidEnd.GetIrModule());

        optimizerList = new ArrayList<>();
        // 这里的顺序是关键的
        optimizerList.add(new RemoveUnReachCode());
        optimizerList.add(new CfgBuilder());
        //optimizerList.add(new RemoveDeadBlock());
        //optimizerList.add(new CfgBuilder());

        optimizerList.add(new RemoveDeadCode());
        optimizerList.add(new CfgBuilder());

        //optimizerList.add(new RemoveDeadBlock());
        //optimizerList.add(new CfgBuilder());

        optimizerList.add(new ScalarizeLocalArray());
        optimizerList.add(new MemToReg());
        optimizerList.add(new CfgBuilder());
        optimizerList.add(new InlineSmallFunction());

        optimizerList.add(new RemoveUnReachCode());
        optimizerList.add(new CfgBuilder());
        optimizerList.add(new RemoveDeadCode());
        optimizerList.add(new CfgBuilder());

        for (int i = 0; i < 5; i++) {
            optimizerList.add(new Lvn());
            optimizerList.add(new RemoveUnReachCode());
            optimizerList.add(new CfgBuilder());
            optimizerList.add(new RemoveDeadCode());
            optimizerList.add(new CfgBuilder());
        }

        optimizerList.add(new ConstantPropagation());
        optimizerList.add(new MemoryValuePropagation());
        optimizerList.add(new RemoveDeadCode());
        optimizerList.add(new CfgBuilder());
        optimizerList.add(new LoopInvariantCodeMotion());
        optimizerList.add(new MemoryValuePropagation());
        optimizerList.add(new Lvn());
        optimizerList.add(new RemoveDeadCode());
        optimizerList.add(new CfgBuilder());

        if (Setting.GRAPH_COLORING) {
            optimizerList.add(new ColoringRegisterAllocator());
        } else {
            optimizerList.add(new ActiveAnalysis());
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
