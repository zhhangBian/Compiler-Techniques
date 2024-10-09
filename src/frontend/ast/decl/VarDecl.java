package frontend.ast.decl;

import frontend.ast.Node;
import frontend.ast.SyntaxType;

import java.util.ArrayList;

public class VarDecl extends Node {
    public VarDecl() {
        super(SyntaxType.VAR_DECL, new ArrayList<>());
    }

    @Override
    public void Parse() {

    }
}
