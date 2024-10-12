package frontend.ast.func;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

public class FuncFormalParamS extends Node {
    public FuncFormalParamS() {
        super(SyntaxType.FUNC_FORMAL_PARAM_S);
    }

    @Override
    public void Parse() {
        this.AddNode(new FuncFormalParam());
        // 多个参数情况
        while (GetCurrentTokenType().equals(TokenType.COMMA)) {
            // ,
            this.AddNode(new TokenNode());
            // FuncFParam
            this.AddNode(new FuncFormalParam());
        }
    }
}
