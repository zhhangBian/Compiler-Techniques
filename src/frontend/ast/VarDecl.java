package frontend.ast;

import java.util.ArrayList;

public class VarDecl extends Node {
    public VarDecl() {
        super(SyntaxType.VAR_DECL, new ArrayList<>());
    }

    @Override
    public void Parse() {

    }
}
