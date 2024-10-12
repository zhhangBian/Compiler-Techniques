package frontend.lexer;

public class Token {
    private final TokenType tokenType;
    private final String stringValue;
    private final int lineNumber;

    public Token(TokenType tokenType, String stringValue, int lineNumber) {
        this.tokenType = tokenType;
        this.stringValue = stringValue;
        this.lineNumber = lineNumber;
    }

    public TokenType GetTokenType() {
        return this.tokenType;
    }

    public String GetStringValue() {
        return this.stringValue;
    }

    public int GetLineNumber() {
        return this.lineNumber;
    }

    @Override
    public String toString() {
        return this.stringValue;
    }
}
