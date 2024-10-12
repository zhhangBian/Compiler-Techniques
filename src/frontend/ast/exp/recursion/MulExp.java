package frontend.ast.exp.recursion;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.UnaryExp;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

public class MulExp extends RecursionNode {
    public MulExp() {
        super(SyntaxType.MUL_EXP);
    }

    public MulExp(Node node1, Node node2, Node node3) {
        super(SyntaxType.MUL_EXP);
        this.components.add(node1);
        this.components.add(node2);
        this.components.add(node3);
    }

    @Override
    public void Parse() {
        this.AddNodeList(new UnaryExp());

        this.AddNode(new UnaryExp());
        while (GetCurrentTokenType().equals(TokenType.MULT) ||
            GetCurrentTokenType().equals(TokenType.DIV) ||
            GetCurrentTokenType().equals(TokenType.MOD)) {
            this.AddNodeList(new TokenNode());
            this.AddNodeList(new UnaryExp());
        }
        this.HandleRecursion(MulExp::new);
    }
}
