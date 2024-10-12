package frontend.ast.exp;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.recursion.AddExp;

public class ConstExp extends Node {
    public ConstExp() {
        super(SyntaxType.CONST_EXP);
    }

    @Override
    public void Parse() {
        this.AddNode(new AddExp());
    }
}
