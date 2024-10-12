package frontend.ast.exp;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.recursion.LOrExp;

public class Cond extends Node {
    public Cond() {
        super(SyntaxType.COND_EXP);
    }

    @Override
    public void Parse() {
        this.AddNode(new LOrExp());
    }
}
