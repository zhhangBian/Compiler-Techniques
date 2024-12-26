package midend.symbol;

import error.Error;
import error.ErrorRecorder;
import error.ErrorType;

import java.util.ArrayList;
import java.util.Hashtable;

public class SymbolTable {
    private final int depth;
    private int index;

    private final ArrayList<Symbol> symbolList;
    private final Hashtable<String, Symbol> symbolTable;

    private final SymbolTable fatherTable;
    private final ArrayList<SymbolTable> sonTables;

    public SymbolTable(int depth, SymbolTable fatherTable) {
        this.depth = depth;
        this.index = -1;

        this.symbolList = new ArrayList<>();
        this.symbolTable = new Hashtable<>();

        this.fatherTable = fatherTable;
        this.sonTables = new ArrayList<>();
    }

    public int GetDepth() {
        return this.depth;
    }

    public Symbol GetSymbol(String symbolName) {
        return this.symbolTable.get(symbolName);
    }

    public SymbolTable GetFatherTable() {
        return this.fatherTable;
    }

    public void AddSonTable(SymbolTable symbolTable) {
        this.sonTables.add(symbolTable);
    }

    public void AddSymbol(Symbol symbol, int line) {
        String symbolName = symbol.GetSymbolName();
        if (!this.symbolTable.containsKey(symbolName)) {
            this.symbolList.add(symbol);
            this.symbolTable.put(symbolName, symbol);
        }
        // 当前层有相同名，重定义
        else {
            ErrorRecorder.AddError(new Error(ErrorType.NAME_REDEFINE, line));
        }
    }

    public SymbolTable GetNextSonTable() {
        return this.sonTables.get(++index);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Symbol symbol : this.symbolList) {
            stringBuilder.append(this.depth + " " + symbol + "\n");
        }

        for (SymbolTable sonTable : this.sonTables) {
            stringBuilder.append(sonTable.toString());
        }

        return stringBuilder.toString();
    }
}
