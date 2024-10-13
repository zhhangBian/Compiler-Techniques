package frontend.ast.token;

import frontend.ast.SyntaxType;

public class FuncType extends BasicTokenNode {
    public FuncType() {
        super(SyntaxType.FUNC_TYPE);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.token.GetTokenType() + " " + this.token.GetStringValue() + "\n");
        stringBuilder.append("<" + this.syntaxType + ">");
        return stringBuilder.toString();
    }
}
