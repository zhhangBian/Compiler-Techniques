package frontend.ast.token;

import frontend.ast.SyntaxType;

public class FuncType extends BasicTokenNode {
    public FuncType() {
        super(SyntaxType.FUNC_TYPE);
    }
}
