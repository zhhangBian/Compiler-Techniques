package frontend.lexer;

import java.util.ArrayList;

public class TokenStream {
    private final ArrayList<Token> tokenList;
    private int readPoint;

    public TokenStream(ArrayList<Token> tokenList) {
        this.tokenList = tokenList;
        this.readPoint = 0;
    }

    public void Read() {
        if (this.readPoint >= this.tokenList.size()) {
            return;
        }
        this.readPoint++;
    }

    public Token Peek(int peekStep) {
        if (this.readPoint + peekStep >= this.tokenList.size()) {
            return null;
        }
        return this.tokenList.get(this.readPoint + peekStep);
    }
}
