package frontend.ast.func;

import frontend.ast.block.Block;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.FuncType;
import frontend.ast.token.Ident;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

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
        this.AddNode(new FuncFormalParamS());
        // )
        this.AddNode(new TokenNode());
        // Block
        this.AddNode(new Block());
    }
}
