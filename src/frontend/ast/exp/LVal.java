package frontend.ast.exp;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.Ident;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

public class LVal extends Node {
    public LVal() {
        super(SyntaxType.LVAL_EXP);
    }

    @Override
    public void Parse() {
        // Ident
        this.AddNode(new Ident());
        // 数组形式
        if (GetCurrentTokenType().equals(TokenType.LBRACK)) {
            // [
            this.AddNode(new TokenNode());
            // Exp
            this.AddNode(new Exp());
            // ]
            if (GetCurrentTokenType().equals(TokenType.RBRACK)) {
                this.AddNode(new TokenNode());
            }
            // TODO：错误处理
        }
    }
}
