package frontend.ast.exp;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.recursion.AddExp;

public class Exp extends Node {
    public Exp() {
        super(SyntaxType.EXP);
    }

    @Override
    public void Parse() {
        this.AddNode(new AddExp());
    }
}
