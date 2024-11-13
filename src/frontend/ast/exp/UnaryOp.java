package frontend.ast.exp;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.TokenNode;

public class UnaryOp extends Node {
    public UnaryOp() {
        super(SyntaxType.UNARY_OP);
    }

    @Override
    public void Parse() {
        this.AddNode(new TokenNode());
    }

    public String GetSimpleName() {
        TokenNode tokenNode = (TokenNode) this.components.get(0);
        return tokenNode.GetSimpleName();
    }
}
