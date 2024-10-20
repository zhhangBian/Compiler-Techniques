package midend.symbol;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    private ArrayList<Symbol> formalParamList;

    public FuncSymbol(String symbolName, SymbolType symbolType) {
        super(symbolName, symbolType);
        this.formalParamList = new ArrayList<>();
    }

    public FuncSymbol(String symbolName, SymbolType symbolType,
                      ArrayList<Symbol> formalParamList) {
        super(symbolName, symbolType);
        this.formalParamList = formalParamList;
    }

    public void SetFormalParamList(ArrayList<Symbol> formalParamList) {
        this.formalParamList = formalParamList;
    }

    public ArrayList<Symbol> GetFormalParamList() {
        return this.formalParamList;
    }
}
