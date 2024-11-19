package midend;

import frontend.FrontEnd;
import frontend.ast.CompUnit;
import midend.llvm.IrBuilder;
import midend.llvm.IrModule;
import midend.symbol.SymbolManger;
import midend.symbol.SymbolTable;
import midend.visit.Visitor;

import java.io.IOException;

public class MidEnd {
    private static CompUnit rootNode;

    public static void GenerateSymbolTable() throws IOException {
        SymbolManger.Init();
        rootNode = FrontEnd.GetAstTree();
        rootNode.Visit();
        SymbolManger.GoBackToRootSymbolTable();
    }

    public static void GenerateIr() {
        IrBuilder.SetCurrentModule(new IrModule());
        Visitor visitor = new Visitor(rootNode);
        visitor.Visit();
        IrBuilder.Check();
    }

    public static SymbolTable GetSymbolTable() {
        return SymbolManger.GetSymbolTable();
    }
}
