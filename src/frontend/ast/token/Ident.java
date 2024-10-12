package frontend.ast.token;

import frontend.ast.SyntaxType;

public class Ident extends BasicTokenNode {
    public Ident() {
        super(SyntaxType.IDENT);
    }
}
