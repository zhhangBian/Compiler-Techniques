package frontend.ast.value;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.IntConst;

public class Number extends Node {
    private int value;

    public Number() {
        super(SyntaxType.NUMBER);
    }

    @Override
    public void Parse() {
        IntConst intConst = new IntConst();
        intConst.Parse();
        this.components.add(intConst);

        this.value = Integer.parseInt(intConst.GetSimpleName());
    }

    public int GetValue() {
        return this.value;
    }
}
