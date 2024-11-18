package frontend.ast.exp.recursion;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

import java.util.ArrayList;

public class LOrExp extends RecursionNode {
    public LOrExp() {
        super(SyntaxType.LOR_EXP);
    }

    public LOrExp(Node node) {
        super(SyntaxType.LOR_EXP);
        this.components.add(node);
    }

    public LOrExp(Node node1, Node node2, Node node3) {
        super(SyntaxType.LOR_EXP);
        this.components.add(node1);
        this.components.add(node2);
        this.components.add(node3);
    }

    @Override
    public void Parse() {
        // LAndExp
        this.AddNodeList(new LAndExp());
        while (GetCurrentTokenType().equals(TokenType.OR)) {
            // ||
            this.AddNodeList(new TokenNode());
            // LAndExp
            this.AddNodeList(new LAndExp());
        }
        this.HandleRecursion(LOrExp::new, LOrExp::new);
    }

    public ArrayList<LAndExp> GetLAndExpList() {
        ArrayList<LAndExp> landExpList = new ArrayList<>();
        for (Node node : this.nodeList) {
            if (node instanceof LAndExp lAndExp) {
                landExpList.add(lAndExp);
            }
        }
        return landExpList;
    }
}
