package frontend.ast;

import frontend.ast.decl.Decl;
import frontend.ast.func.FuncDef;
import frontend.ast.func.MainFuncDef;
import frontend.lexer.TokenType;

import java.util.ArrayList;

public class CompUnit extends Node {
    public CompUnit() {
        super(SyntaxType.COMP_UNIT);
    }

    @Override
    public void Parse() {
        while (GetCurrentToken() != null) {
            // 主成分
            if (Peek(1).GetTokenType().equals(TokenType.MAINTK)) {
                this.AddNode(new MainFuncDef());
            }
            // 函数
            else if (Peek(2).GetTokenType().equals(TokenType.LPARENT)) {
                this.AddNode(new FuncDef());
            }
            // 常量
            else if (GetCurrentTokenType().equals(TokenType.CONSTTK) ||
                GetCurrentTokenType().equals(TokenType.INTTK) ||
                GetCurrentTokenType().equals(TokenType.CHARTK)) {
                this.AddNode(new Decl());
            }
            // 未定义
            else {
                break;
            }
        }
    }

    public ArrayList<Decl> GetDecls() {
        ArrayList<Decl> decls = new ArrayList<>();
        for (Node node : this.components) {
            if (node instanceof Decl decl) {
                decls.add(decl);
            }
        }
        return decls;
    }

    public ArrayList<FuncDef> GetFuncDefs() {
        ArrayList<FuncDef> funcDefs = new ArrayList<>();
        for (Node node : this.components) {
            if (node instanceof FuncDef funcDef) {
                funcDefs.add(funcDef);
            }
        }
        return funcDefs;
    }

    public MainFuncDef GetMainFuncDef() {
        for (Node node : this.components) {
            if (node instanceof MainFuncDef mainFuncDef) {
                return mainFuncDef;
            }
        }
        throw new RuntimeException("no MainFuncDef in the CompUnit!");
    }
}
