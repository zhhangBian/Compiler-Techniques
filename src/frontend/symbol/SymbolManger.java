package frontend.symbol;

import frontend.ast.Node;

public class SymbolManger {
    private final SymbolTable rootSymbolTable;
    private Node rootNode;

    private SymbolTable nowSymbolTable;
    private int depth;

    public SymbolManger() {
        this.rootSymbolTable = new SymbolTable(1, null);
        this.depth = 1;
    }

    public void SetRootNode(Node rootNode) {
        this.rootNode = rootNode;
    }

    public void GenerateSymbolTable() {

    }

    public SymbolTable GetRootSymbolTable() {
        return rootSymbolTable;
    }

    private void GoToFather() {
        SymbolTable fatherTable = this.nowSymbolTable.GetFatherTable();
        if (fatherTable != null) {
            this.nowSymbolTable = fatherTable;
        }
    }

    private void GoToSon() {
        SymbolTable sonTable = new SymbolTable(++depth, this.nowSymbolTable);
        this.nowSymbolTable.AddSonTable(sonTable);
        this.nowSymbolTable = sonTable;
    }
}
