package frontend.ast;

import java.util.ArrayList;

public class ConstDecl extends Node {
    public ConstDecl() {
        super(SyntaxType.CONST_DECL, new ArrayList<>());
    }

    @Override
    public void Parse() {

    }
}
