package midend.symbol;

public class SymbolManger {
    private static SymbolTable rootSymbolTable;
    private static SymbolTable currentSymbolTable;
    private static int depth;

    public SymbolManger() {
        depth = 1;
        rootSymbolTable = new SymbolTable(depth, null);
        currentSymbolTable = rootSymbolTable;
    }

    public static void AddSymbol(Symbol symbol) {
        currentSymbolTable.AddSymbol(symbol);
    }

    public SymbolTable GetSymbolTable() {
        return rootSymbolTable;
    }

    public static SymbolTable GetCurrentSymbolTable() {
        return currentSymbolTable;
    }

    public static void GoToFatherSymbolTable() {
        SymbolTable fatherTable = currentSymbolTable.GetFatherTable();
        if (fatherTable != null) {
            currentSymbolTable = fatherTable;
        }
    }

    public static void GoToSonSymbolTable() {
        SymbolTable sonTable = new SymbolTable(++depth, currentSymbolTable);
        currentSymbolTable.AddSonTable(sonTable);
        currentSymbolTable = sonTable;
    }
}
