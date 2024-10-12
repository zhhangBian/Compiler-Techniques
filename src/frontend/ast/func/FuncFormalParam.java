package frontend.ast.func;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.BType;
import frontend.ast.token.Ident;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

public class FuncFormalParam extends Node {
    public FuncFormalParam() {
        super(SyntaxType.FUNC_FORMAL_PARAM);
    }

    @Override
    public void Parse() {
        this.AddNode(new BType());
        this.AddNode(new Ident());
        // 数组形，预留多维的形式
        while (GetCurrentTokenType().equals(TokenType.LBRACK)) {
            this.AddNode(new TokenNode());
            if (GetCurrentTokenType().equals(TokenType.RBRACK)) {
                this.AddNode(new TokenNode());
            }
            // TODO：错误处理
        }
    }
}
