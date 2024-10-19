package frontend.ast.func;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.Exp;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

public class FuncRealParamS extends Node {
    public FuncRealParamS() {
        super(SyntaxType.FUNC_REAL_PARAM_S);
    }

    @Override
    public void Parse() {
        this.AddNode(new Exp());
        while (GetCurrentTokenType().equals(TokenType.COMMA)) {
            // ,
            this.AddNode(new TokenNode());
            // Exp
            this.AddNode(new Exp());
        }
    }

    public int GetRealParamCount() {
        int count = 0;
        for (Node component : this.components) {
            if (component instanceof Exp) {
                count++;
            }
        }
        return count;
    }
}
