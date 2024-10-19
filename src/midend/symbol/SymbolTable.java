package midend.symbol;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

public class SymbolTable {
    private final int depth;
    private final ArrayList<Symbol> symbolList;
    private final HashSet<String> symbolNameSet;

    private final SymbolTable fatherTable;
    private final ArrayList<SymbolTable> sonTables;

    public SymbolTable(int depth, SymbolTable fatherTable) {
        this.depth = depth;
        this.symbolList = new ArrayList<>();
        this.symbolNameSet = new HashSet<>();

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
        // TODO：处理命名冲突问题
        String symbolName = symbol.GetSymbolName();
        if (!this.symbolNameSet.contains(symbolName)) {
            this.symbolList.add(symbol);
            this.symbolNameSet.add(symbolName);
        } else {
            throw new RuntimeException("name conflict in symbolTable");
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Symbol symbol : this.symbolList) {
            stringBuilder.append(this.depth + " " + symbol.GetSymbolName() + " " +
                symbol.GetSymbolType() + "\n");
        }

        for (SymbolTable sonTable : this.sonTables) {
            stringBuilder.append(sonTable.toString());
        }

        return stringBuilder.toString();
    }
}
