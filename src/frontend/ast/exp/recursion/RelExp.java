package frontend.ast.exp.recursion;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

import java.util.ArrayList;

public class RelExp extends RecursionNode {
    public RelExp() {
        super(SyntaxType.REL_EXP);
    }

    public RelExp(Node node) {
        super(SyntaxType.REL_EXP);
        this.components.add(node);
    }

    public RelExp(Node node1, Node node2, Node node3) {
        super(SyntaxType.REL_EXP);
        this.components.add(node1);
        this.components.add(node2);
        this.components.add(node3);
    }

    @Override
    public void Parse() {
        // AddExp
        this.AddNodeList(new AddExp());
        while (GetCurrentTokenType().equals(TokenType.LSS) ||
            GetCurrentTokenType().equals(TokenType.LEQ) ||
            GetCurrentTokenType().equals(TokenType.GRE) ||
            GetCurrentTokenType().equals(TokenType.GEQ)) {
            // < | > | <= | >=
            this.AddNodeList(new TokenNode());
            // AddExp
            this.AddNodeList(new AddExp());
        }
        this.HandleRecursion(RelExp::new, RelExp::new);
    }

    public ArrayList<AddExp> GetAddExpList() {
        ArrayList<AddExp> addExpList = new ArrayList<>();
        for (Node node : this.nodeList) {
            if (node instanceof AddExp addExp) {
                addExpList.add(addExp);
            }
        }
        return addExpList;
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
