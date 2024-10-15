package frontend.symbol;

import java.util.ArrayList;
import java.util.Hashtable;

public class SymbolTable {
    private final int depth;
    private final ArrayList<Symbol> symbolList;
    private final Hashtable<String, Symbol> nameTable;

    private final SymbolTable fatherTable;
    private final ArrayList<SymbolTable> sonTables;

    public SymbolTable(int depth, SymbolTable fatherTable) {
        this.depth = depth;
        this.symbolList = new ArrayList<>();
        this.nameTable = new Hashtable<>();

        this.fatherTable = fatherTable;
        this.sonTables = new ArrayList<>();
    }

    public int GetDepth() {
        return this.depth;
    }

    public SymbolTable GetFatherTable() {
        return this.fatherTable;
    }

    public void AddSonTable(SymbolTable symbolTable) {
        this.sonTables.add(symbolTable);
    }

    public void AddSymbol(Symbol symbol) {
        this.symbolList.add(symbol);
        // TODO：处理命名冲突问题
        this.nameTable.put(symbol.GetSymbolName(), symbol);
    }
}
