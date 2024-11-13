package frontend.ast.exp;

import frontend.ast.Node;
import frontend.ast.SyntaxType;

public abstract class ComputeExp extends Node {
    protected boolean isConst;
    protected int value;

    public ComputeExp(SyntaxType type) {
        super(type);
        this.isConst = false;
        this.value = 0;
    }

    public abstract void Compute();

    public boolean GetIfConst() {
        return this.isConst;
    }

    public int GetValue() {
        return this.value;
    }
}
