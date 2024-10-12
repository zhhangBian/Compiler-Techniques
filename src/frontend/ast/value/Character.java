package frontend.ast.value;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.CharConst;

public class Character extends Node {
    public Character() {
        super(SyntaxType.CHARACTER);
    }

    @Override
    public void Parse() {
        this.AddNode(new CharConst());
    }
}
