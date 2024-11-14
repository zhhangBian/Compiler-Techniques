package midend;

import frontend.FrontEnd;
import frontend.ast.CompUnit;
import midend.llvm.IrBuilder;
import midend.llvm.IrModule;
import midend.symbol.SymbolManger;
import midend.symbol.SymbolTable;
import midend.visit.Visitor;

public class MidEnd {
    private static CompUnit rootNode;

    public static void Visit() {
        SymbolManger.Init();
        rootNode = FrontEnd.GetAstTree();
        rootNode.Visit();
        SymbolManger.GoBackToRootSymbolTable();

        IrBuilder.SetCurrentModule(new IrModule());
        Visitor visitor = new Visitor(rootNode);
        visitor.Visit();
    }

    public static SymbolTable GetSymbolTable() {
        return SymbolManger.GetSymbolTable();
    }
}
