package frontend.ast.exp.recursion;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

public class EqExp extends RecursionNode {
    public EqExp() {
        super(SyntaxType.EQ_EXP);
    }

    public EqExp(Node node1, Node node2, Node node3) {
        super(SyntaxType.EQ_EXP);
        this.components.add(node1);
        this.components.add(node2);
        this.components.add(node3);
    }

    @Override
    public void Parse() {
        this.AddNodeList(new RelExp());
        while (GetCurrentTokenType().equals(TokenType.EQL) ||
            GetCurrentTokenType().equals(TokenType.NEQ)) {
            this.AddNodeList(new TokenNode());
            this.AddNodeList(new RelExp());
        }
        this.HandleRecursion(EqExp::new);
    }
}
