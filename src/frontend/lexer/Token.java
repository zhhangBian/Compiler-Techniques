package frontend.lexer;

public class Token {
    private String value;
    private TokenType type;
    private int lineNum;

    public Token(String value, TokenType type, int lineNum) {
        this.value = value;
        this.type = type;
        this.lineNum = lineNum;
    }
}
