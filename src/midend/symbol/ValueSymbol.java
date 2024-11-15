package midend.symbol;

import java.util.ArrayList;

public class ValueSymbol extends Symbol {
    private final int dimension;
    private final ArrayList<Integer> depthList;
    private final ArrayList<Integer> initValueList;
    private boolean isGlobal;

    public ValueSymbol(String symbolName, SymbolType symbolType) {
        super(symbolName, symbolType);
        this.dimension = 0;
        this.depthList = new ArrayList<>();
        this.initValueList = new ArrayList<>();
        this.isGlobal = false;
    }

    public ValueSymbol(String symbolName, SymbolType symbolType,
                       int dimension, ArrayList<Integer> depthList) {
        super(symbolName, symbolType);
        this.dimension = dimension;
        this.depthList = depthList;
        this.initValueList = new ArrayList<>();
        this.isGlobal = false;
    }

    public ValueSymbol(String symbolName, SymbolType symbolType, int dimension,
                       ArrayList<Integer> depthList, ArrayList<Integer> initValueList) {
        super(symbolName, symbolType);
        this.dimension = dimension;
        this.depthList = depthList;
        this.initValueList = initValueList == null ? new ArrayList<>() : initValueList;
        this.isGlobal = false;
    }

    public void SetIsGlobal(boolean isGlobal) {
        this.isGlobal = isGlobal;
    }

    public boolean IsGlobal() {
        return this.isGlobal;
    }

    public int GetDimension() {
        return this.dimension;
    }

    public int GetTotalDepth() {
        int totalDepth = 1;
        for (int depth : this.depthList) {
            totalDepth *= depth;
        }
        return totalDepth;
    }

    public ArrayList<Integer> GetInitValueList() {
        return this.initValueList;
    }

    public ArrayList<Integer> GetDepthList() {
        return this.depthList;
    }
}
