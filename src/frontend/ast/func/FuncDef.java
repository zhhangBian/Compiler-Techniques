package frontend.ast.func;

import frontend.ast.block.Block;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.FuncType;
import frontend.ast.token.Ident;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;
import midend.symbol.FuncSymbol;
import midend.symbol.SymbolManger;
import midend.symbol.SymbolType;

public class FuncDef extends Node {
    public FuncDef() {
        super(SyntaxType.FUNC_DEF);
    }

    @Override
    public void Parse() {
        // FuncType
        this.AddNode(new FuncType());
        // Ident
        this.AddNode(new Ident());
        // (
        this.AddNode(new TokenNode());
        // FuncFParams
        if (GetCurrentTokenType().equals(TokenType.INTTK) ||
            GetCurrentTokenType().equals(TokenType.CHARTK)) {
            this.AddNode(new FuncFormalParamS());
        }
        // )
        if (GetCurrentTokenType().equals(TokenType.RPARENT)) {
            this.AddNode(new TokenNode());
        } else {
            this.AddMissRParentError();
        }
        // Block
        this.AddNode(new Block());
    }

    @Override
    public void GenerateIr() {
        String type = ((FuncType) this.components.get(0)).GetTokenString();
        String symbolName = ((Ident) this.components.get(1)).GetTokenString();
        SymbolManger.AddSymbol(new FuncSymbol(symbolName, SymbolType.GetFuncType(type)));
        // 解析形参和函数体的符号
        SymbolManger.GoToSonSymbolTable();
        for (Node component : this.components) {
            if (component instanceof FuncFormalParamS ||
                component instanceof Block) {
                component.GenerateIr();
            }
        }
        SymbolManger.GoToFatherSymbolTable();
    }
}
