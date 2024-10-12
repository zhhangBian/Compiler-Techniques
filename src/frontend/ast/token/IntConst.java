package frontend.ast.token;

import frontend.ast.SyntaxType;

public class IntConst extends BasicTokenNode {
    public IntConst() {
        super(SyntaxType.INT_CONST);
    }
}
