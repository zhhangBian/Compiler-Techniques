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
    private static IrModule irModule;

    public static void GenerateSymbolTable() {
        SymbolManger.Init();
        rootNode = FrontEnd.GetAstTree();
        rootNode.Visit();
        SymbolManger.GoBackToRootSymbolTable();
    }

    public static void GenerateIr() {
        irModule = new IrModule();
        IrBuilder.SetCurrentModule(irModule);
        Visitor visitor = new Visitor(rootNode);
        visitor.Visit();
        IrBuilder.Check();
    }

    public static SymbolTable GetSymbolTable() {
        return SymbolManger.GetSymbolTable();
    }

    public static IrModule GetIrModule() {
        return irModule;
    }
}
