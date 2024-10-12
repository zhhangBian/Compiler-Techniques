package frontend.ast.exp;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.TokenNode;

public class ForStmt extends Node {
    public ForStmt() {
        super(SyntaxType.FOR_STMT);
    }

    @Override
    public void Parse() {
        // LVal
        this.AddNode(new LVal());
        // =
        this.AddNode(new TokenNode());
        // Exp
        this.AddNode(new Exp());
    }
}
