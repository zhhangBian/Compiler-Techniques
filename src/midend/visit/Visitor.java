package midend.visit;

import frontend.ast.CompUnit;
import frontend.ast.decl.Decl;
import frontend.ast.func.FuncDef;
import frontend.ast.func.MainFuncDef;
import midend.llvm.IrBuilder;
import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrFunction;

import java.util.ArrayList;

public class Visitor {
    private final CompUnit compUnit;

    public Visitor(CompUnit compUnit) {
        this.compUnit = compUnit;
    }

    public void Visit() {
        ArrayList<Decl> decls = this.compUnit.GetDecls();
        ArrayList<FuncDef> funcDefs = this.compUnit.GetFuncDefs();
        MainFuncDef mainFuncDef = this.compUnit.GetMainFuncDef();

        FuncVisitDecl funcVisitDecl = new FuncVisitDecl();
        for (Decl decl : decls) {
            funcVisitDecl.VisitDecl(decl);
        }

    }
}
