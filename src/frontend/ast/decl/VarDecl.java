package frontend.ast.decl;

import frontend.ast.token.BType;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

public class VarDecl extends Node {
    public VarDecl() {
        super(SyntaxType.VAR_DECL);
    }

    @Override
    public void Parse() {
        // BType
        this.AddNode(new BType());
        // VarDef
        this.AddNode(new VarDef());

        // 重复的constDef
        while (GetCurrentTokenType().equals(TokenType.COMMA)) {
            // ,
            this.AddNode(new TokenNode());
            // ConstDef
            this.AddNode(new VarDef());
        }

        // ;
        this.AddNode(new TokenNode());
        // TODO：错误处理
    }
}
