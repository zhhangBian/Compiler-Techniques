package midend;

import frontend.FrontEnd;
import frontend.ast.Node;
import midend.symbol.SymbolManger;
import midend.symbol.SymbolTable;

public class MidEnd {
    private static SymbolManger symbolManger;
    private static Node rootNode;

    public static void GenerateIr() {
        symbolManger = new SymbolManger();
        rootNode = FrontEnd.GetAstTree();
        rootNode.GenerateIr();
    }

    public static SymbolTable GetSymbolTable() {
        return symbolManger.GetSymbolTable();
    }
}
