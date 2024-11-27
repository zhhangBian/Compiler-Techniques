package optimize;

import java.util.ArrayList;

public class OptimizeManager {
    private final ArrayList<Optimizer> optimizerList;

    public OptimizeManager() {
        this.optimizerList = new ArrayList<>();
    }

    public void Optimize() {
        for (Optimizer optimizer : this.optimizerList) {
            optimizer.Optimize();
        }
    }
}
