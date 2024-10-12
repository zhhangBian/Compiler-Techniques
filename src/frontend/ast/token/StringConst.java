package frontend.ast.token;

import frontend.ast.SyntaxType;

public class StringConst extends BasicTokenNode {
    public StringConst() {
        super(SyntaxType.STRING_CONST);
    }
}
