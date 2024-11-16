package frontend.ast.exp;

import frontend.ast.SyntaxType;
import frontend.ast.exp.recursion.LOrExp;

public class Cond extends ComputeExp {
    public Cond() {
        super(SyntaxType.COND_EXP);
    }

    @Override
    public void Parse() {
        this.AddNode(new LOrExp());
    }

    @Override
    public void Compute() {
        // 递归进行计算
        LOrExp lorExp = (LOrExp) this.components.get(0);
        lorExp.Compute();
        // 设置相应的属性
        this.isConst = lorExp.GetIfConst();
        this.value = lorExp.GetValue();
    }

    public LOrExp GetLOrExp() {
        return (LOrExp) this.components.get(0);
    }
}
