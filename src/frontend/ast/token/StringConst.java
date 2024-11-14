package frontend.ast.token;

import frontend.ast.SyntaxType;

public class StringConst extends BasicTokenNode {
    private String string;

    public StringConst() {
        super(SyntaxType.STRING_CONST);
    }

    @Override
    public void Parse() {
        this.token = GetCurrentToken();
        Read();
        this.string = this.token.GetStringValue();
    }

    public String GetStringValue() {
        return this.string;
    }
}
