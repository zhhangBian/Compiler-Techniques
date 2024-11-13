package frontend.ast.exp;

import frontend.ast.SyntaxType;
import frontend.ast.exp.recursion.AddExp;

public class ConstExp extends ComputeExp {
    public ConstExp() {
        super(SyntaxType.CONST_EXP);
    }

    @Override
    public void Parse() {
        this.AddNode(new AddExp());
    }

    @Override
    public void Compute() {

    }
}
