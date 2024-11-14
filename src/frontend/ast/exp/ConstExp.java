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
        this.Compute();
    }

    @Override
    public void Compute() {
        // 递归进行计算
        AddExp addExp = (AddExp) this.components.get(0);
        addExp.Compute();
        // 设置相应的属性
        this.isConst = addExp.GetIfConst();
        this.value = addExp.GetValue();
    }
}
