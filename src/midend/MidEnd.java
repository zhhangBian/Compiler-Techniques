package midend;

import frontend.FrontEnd;
import frontend.ast.CompUnit;
import midend.llvm.IrModule;
import midend.symbol.SymbolManger;
import midend.symbol.SymbolTable;

public class MidEnd {
    private static SymbolManger symbolManger;
    private static IrModule irModule;
    private static CompUnit rootNode;

    public static void Visit() {
        symbolManger = new SymbolManger();
        irModule = new IrModule();
        rootNode = FrontEnd.GetAstTree();
        rootNode.Visit();
    }

    public static SymbolTable GetSymbolTable() {
        return symbolManger.GetSymbolTable();
    }
}
