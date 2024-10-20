package midend.symbol;

public class Symbol {
    private final String symbolName;
    private final SymbolType symbolType;

    public Symbol(String symbolName, SymbolType symbolType) {
        this.symbolName = symbolName;
        this.symbolType = symbolType;
    }

    public String GetSymbolName() {
        return this.symbolName;
    }

    public SymbolType GetSymbolType() {
        return this.symbolType;
    }

    @Override
    public String toString() {
        return symbolName + " " + symbolType.toString();
    }
}
