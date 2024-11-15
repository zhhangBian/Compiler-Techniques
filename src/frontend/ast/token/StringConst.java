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
        // 去除首位的引号
        this.string = this.token.GetStringValue();
        this.string = this.string.substring(1, this.string.length() - 1);
    }

    public String GetStringValue() {
        return this.string;
    }
}
