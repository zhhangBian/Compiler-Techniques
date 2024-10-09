package frontend.lexer;

import java.util.ArrayList;

public class TokenStream {
    private final ArrayList<Token> tokenList;
    private int readPoint;

    public TokenStream(ArrayList<Token> tokenList) {
        this.tokenList = tokenList;
        this.readPoint = 0;
    }

    public Token Read() {
        if (this.readPoint >= this.tokenList.size()) {
            // TODO：应该更优雅的实现方式
            return null;
        }
        return tokenList.get(readPoint++);
    }

    public void UnRead() {
        if (this.readPoint > 0) {
            this.readPoint--;
        }
    }
}
