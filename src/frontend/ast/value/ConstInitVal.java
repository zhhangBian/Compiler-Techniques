package frontend.ast.value;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.ConstExp;
import frontend.ast.token.StringConst;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

public class ConstInitVal extends Node {
    public ConstInitVal() {
        super(SyntaxType.CONST_INIT_VAL);
    }

    @Override
    public void Parse() {
        // {...} 形式的初值
        if (GetCurrentTokenType().equals(TokenType.LBRACE)) {
            // {
            this.AddNode(new TokenNode());
            // value
            if (GetCurrentTokenType().equals(TokenType.RBRACE)) {
                // ConstExp
                this.AddNode(new ConstExp());
                while (GetCurrentTokenType().equals(TokenType.COMMA)) {
                    // ,
                    this.AddNode(new TokenNode());
                    // ConstExp
                    this.AddNode(new ConstExp());
                }
            }
            // }
            this.AddNode(new TokenNode());
        }
        // StringConst
        else if (GetCurrentTokenType().equals(TokenType.STRCON)) {
            this.AddNode(new StringConst());
        }
        // ConstExp
        else {
            this.AddNode(new ConstExp());
        }
    }
}
