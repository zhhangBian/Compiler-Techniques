package frontend.ast;

import frontend.lexer.Token;

import java.util.ArrayList;

public class TokenNode extends Node {
    private final Token token;

    public TokenNode(Token token) {
        super(SyntaxType.TOKEN, new ArrayList<>());
        this.token = token;
    }

    public Token GetToken() {
        return this.token;
    }

    @Override
    public void Parse() {

    }
}
