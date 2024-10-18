package midend.symbol;

public class SymbolManger {
    private static SymbolTable rootSymbolTable;
    private static SymbolTable currentSymbolTable;
    private static int depth;

    public SymbolManger() {
        rootSymbolTable = new SymbolTable(1, null);
        depth = 1;
    }

    public SymbolTable GetRootSymbolTable() {
        return rootSymbolTable;
    }

    public static SymbolTable GetCurrentSymbolTable() {
        return currentSymbolTable;
    }

    private void GoToFatherSymbolTable() {
        SymbolTable fatherTable = currentSymbolTable.GetFatherTable();
        if (fatherTable != null) {
            currentSymbolTable = fatherTable;
        }
    }

    private void GoToSonSymbolTable() {
        SymbolTable sonTable = new SymbolTable(++depth, currentSymbolTable);
        currentSymbolTable.AddSonTable(sonTable);
        currentSymbolTable = sonTable;
    }
}
