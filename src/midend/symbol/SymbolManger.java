package midend.symbol;

public class SymbolManger {
    private static SymbolTable rootSymbolTable;
    private static SymbolTable currentSymbolTable;
    private static int depth;

    private static int forDepth;
    private static String funcReturnType = "";

    public SymbolManger() {
        depth = 1;
        rootSymbolTable = new SymbolTable(depth, null);
        currentSymbolTable = rootSymbolTable;

        forDepth = 0;
        funcReturnType = "";
    }

    public static void AddSymbol(Symbol symbol, int line) {
        currentSymbolTable.AddSymbol(symbol, line);
    }

    public static Symbol GetSymbol(String name) {
        SymbolTable table = currentSymbolTable;
        while (table != null) {
            Symbol symbol = table.GetSymbol(name);
            if (symbol != null) {
                return symbol;
            }
            table = table.GetFatherTable();
        }
        return null;
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

    public static void EnterForBlock() {
        forDepth++;
    }

    public static void LeaveForBlock() {
        forDepth--;
    }

    public static boolean InForBlock() {
        return forDepth > 0;
    }

    public static void EnterFunc(String type) {
        funcReturnType = type;
    }

    public static void LeaveFunc() {
        funcReturnType = "";
    }

    public static String GetFuncType() {
        return funcReturnType;
    }
}
