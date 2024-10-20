package midend.symbol;

import java.util.ArrayList;

public class ValueSymbol extends Symbol {
    private final int dimension;
    private final ArrayList<Integer> depthList;
    private int initValue;

    public ValueSymbol(String symbolName, SymbolType symbolType) {
        super(symbolName, symbolType);
        this.dimension = 0;
        this.depthList = new ArrayList<>();
        this.initValue = 0;
    }

    public ValueSymbol(String symbolName, SymbolType symbolType, int initValue) {
        super(symbolName, symbolType);
        this.dimension = 0;
        this.depthList = new ArrayList<>();
        this.initValue = initValue;
    }

    public ValueSymbol(String symbolName, SymbolType symbolType,
                       int dimension, ArrayList<Integer> depthList) {
        super(symbolName, symbolType);
        this.dimension = dimension;
        this.depthList = depthList;
        this.initValue = 0;
    }

    public ValueSymbol(String symbolName, SymbolType symbolType,
                       int dimension, ArrayList<Integer> depthList, int initValue) {
        super(symbolName, symbolType);
        this.dimension = dimension;
        this.depthList = depthList;
        this.initValue = 0;
        this.initValue = initValue;
    }

    public int GetDimension() {
        return this.dimension;
    }

    public int GetInitValue() {
        return this.initValue;
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
