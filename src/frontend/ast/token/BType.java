package frontend.ast.token;

import frontend.ast.SyntaxType;

public class BType extends BasicTokenNode {
    public BType() {
        super(SyntaxType.BTYPE);
        this.printSelf = false;
    }
}
