package midend.visit;

import frontend.ast.CompUnit;
import frontend.ast.decl.Decl;
import frontend.ast.func.FuncDef;
import frontend.ast.func.MainFuncDef;

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

        for (Decl decl : decls) {
            VisitorDecl.VisitDecl(decl);
        }

        for (FuncDef funcDef : funcDefs) {
            VisitorFuncDef.VisitFuncDef(funcDef);
        }

        VisitorFuncDef.VisitMainFuncDef(mainFuncDef);
    }
}
