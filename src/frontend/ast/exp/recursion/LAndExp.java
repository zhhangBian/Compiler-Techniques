package frontend.ast.exp.recursion;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

import java.util.ArrayList;

public class LAndExp extends RecursionNode {
    public LAndExp() {
        super(SyntaxType.LAND_EXP);
    }

    public LAndExp(Node node) {
        super(SyntaxType.LAND_EXP);
        this.components.add(node);
    }

    public LAndExp(Node node1, Node node2, Node node3) {
        super(SyntaxType.LAND_EXP);
        this.components.add(node1);
        this.components.add(node2);
        this.components.add(node3);
    }

    @Override
    public void Parse() {
        // EqExp
        this.AddNodeList(new EqExp());
        while (GetCurrentTokenType().equals(TokenType.AND)) {
            // &&
            this.AddNodeList(new TokenNode());
            // EqExp
            this.AddNodeList(new EqExp());
        }
        this.HandleRecursion(LAndExp::new, LAndExp::new);
    }

    public ArrayList<EqExp> GetEqExpList() {
        ArrayList<EqExp> eqExpList = new ArrayList<>();
        for (Node node : this.nodeList) {
            if (node instanceof EqExp eqExp) {
                eqExpList.add(eqExp);
            }
        }
        return eqExpList;
    }
}
