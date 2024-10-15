package frontend.ast.decl;

import frontend.ast.token.BType;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

public class ConstDecl extends Node {
    public ConstDecl() {
        super(SyntaxType.CONST_DECL);
    }

    @Override
    public void Parse() {
        // 得到const
        this.AddNode(new TokenNode());
        // BType
        this.AddNode(new BType());
        // ConstDef
        this.AddNode(new ConstDef());

        // 重复的constDef
        while (GetCurrentTokenType().equals(TokenType.COMMA)) {
            // ,
            this.AddNode(new TokenNode());
            // ConstDef
            this.AddNode(new ConstDef());
        }

        // ;
        if (GetCurrentTokenType().equals(TokenType.SEMICN)) {
            this.AddNode(new TokenNode());
        } else {
            this.AddMissSemicnError();
        }
    }
}
