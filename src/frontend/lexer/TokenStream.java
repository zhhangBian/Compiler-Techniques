package frontend.lexer;

import java.util.ArrayList;

public class TokenStream {
    private final ArrayList<Token> tokenList;
    private int readPoint;
    private int backPoint;

    public TokenStream(ArrayList<Token> tokenList) {
        this.tokenList = tokenList;
        this.readPoint = 0;
        this.backPoint = 0;
    }

    public void Read() {
        if (this.readPoint >= this.tokenList.size()) {
            return;
        }
        this.readPoint++;
    }

    public Token Peek(int peekStep) {
        if (this.readPoint + peekStep >= this.tokenList.size()) {
            return new Token(TokenType.EOF, "end of token stream", -1);
        }
        return this.tokenList.get(this.readPoint + peekStep);
    }

    public void SetBackPoint() {
        this.backPoint = this.readPoint;
    }

    public void GoToBackPoint() {
        this.readPoint = this.backPoint;
    }
}
