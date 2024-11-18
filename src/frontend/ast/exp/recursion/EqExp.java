package frontend.ast.exp.recursion;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

import java.util.ArrayList;

public class EqExp extends RecursionNode {
    public EqExp() {
        super(SyntaxType.EQ_EXP);
    }

    public EqExp(Node node) {
        super(SyntaxType.EQ_EXP);
        this.components.add(node);
    }

    public EqExp(Node node1, Node node2, Node node3) {
        super(SyntaxType.EQ_EXP);
        this.components.add(node1);
        this.components.add(node2);
        this.components.add(node3);
    }

    @Override
    public void Parse() {
        // RelExp
        this.AddNodeList(new RelExp());
        while (GetCurrentTokenType().equals(TokenType.EQL) ||
            GetCurrentTokenType().equals(TokenType.NEQ)) {
            // == | !=
            this.AddNodeList(new TokenNode());
            // RelExp
            this.AddNodeList(new RelExp());
        }
        this.HandleRecursion(EqExp::new, EqExp::new);
    }

    public ArrayList<RelExp> GetRelExpList() {
        ArrayList<RelExp> relExpList = new ArrayList<>();
        for (Node node : this.nodeList) {
            if (node instanceof RelExp relExp) {
                relExpList.add(relExp);
            }
        }
        return relExpList;
    }

    public ArrayList<String> GetOpList() {
        ArrayList<String> opList = new ArrayList<>();
        for (Node node : this.nodeList) {
            if (node instanceof TokenNode op) {
                opList.add(op.GetTokenString());
            }
        }
        return opList;
    }
}
