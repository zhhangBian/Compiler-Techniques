package midend;

import midend.symbol.SymbolManger;
import midend.symbol.SymbolTable;

public class MidEnd {
    private static SymbolManger symbolManger;

    public static void CreateSymbolManager() {
        symbolManger = new SymbolManger();
    }

    public static void GenerateSymbolTable() {

    }

    public static SymbolTable GetRootSymbolTable() {
        return symbolManger.GetRootSymbolTable();
    }
}
