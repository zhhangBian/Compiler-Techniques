package frontend.ast.exp.recursion;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

public class AddExp extends RecursionNode {
    public AddExp() {
        super(SyntaxType.ADD_EXP);
    }

    public AddExp(Node node) {
        super(SyntaxType.ADD_EXP);
        this.components.add(node);
    }

    public AddExp(Node node1, Node node2, Node node3) {
        super(SyntaxType.ADD_EXP);
        this.components.add(node1);
        this.components.add(node2);
        this.components.add(node3);
    }

    @Override
    public void Parse() {
        this.AddNodeList(new MulExp());
        while (GetCurrentTokenType().equals(TokenType.PLUS) ||
            GetCurrentTokenType().equals(TokenType.MINU)) {
            // + | -
            this.AddNodeList(new TokenNode());
            // MulExp
            this.AddNodeList(new MulExp());
        }
        this.HandleRecursion(AddExp::new, AddExp::new);
    }
}
