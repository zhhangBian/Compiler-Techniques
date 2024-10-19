package midend.symbol;

import java.util.ArrayList;

public class ValueSymbol extends Symbol {
    private final int dimension;
    private final ArrayList<Integer> depthList;

    public ValueSymbol(String symbolName, SymbolType symbolType) {
        super(symbolName, symbolType);
        this.dimension = 0;
        this.depthList = new ArrayList<>();
    }

    public ValueSymbol(String symbolName, SymbolType symbolType,
                       int dimension, ArrayList<Integer> depthList) {
        super(symbolName, symbolType);
        this.dimension = dimension;
        this.depthList = depthList;
    }

    public int GetDimension() {
        return this.dimension;
    }

    public ArrayList<Integer> GetDepthList() {
        return this.depthList;
    }

    public int GetTotalDepth() {
        if (this.depthList.isEmpty()) {
            return 0;
        } else {
            int totalDepth = 1;
            for (int depth : this.depthList) {
                totalDepth *= depth;
            }
            return totalDepth;
        }
    }
}
