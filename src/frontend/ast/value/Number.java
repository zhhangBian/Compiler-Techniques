package frontend.ast.value;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.IntConst;

public class Number extends Node {
    public Number() {
        super(SyntaxType.NUMBER);
    }

    @Override
    public void Parse() {
        this.AddNode(new IntConst());
    }
}
