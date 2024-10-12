package frontend.ast.token;

import frontend.ast.SyntaxType;

public class CharConst extends BasicTokenNode {
    public CharConst() {
        super(SyntaxType.CHAR_CONST);
    }
}
