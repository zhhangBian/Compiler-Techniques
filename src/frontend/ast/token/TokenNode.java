package frontend.ast.token;

import frontend.ast.SyntaxType;
import frontend.lexer.Token;

public class TokenNode extends BasicTokenNode {
    public TokenNode() {
        super(SyntaxType.TOKEN);
    }

    public TokenNode(Token token) {
        super(SyntaxType.TOKEN);
        this.token = token;
    }
}
