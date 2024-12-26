package frontend.lexer;

import java.util.ArrayList;
import java.util.Stack;

public class TokenStream {
    private final ArrayList<Token> tokenList;
    private int readPoint;
    // 回溯栈
    private final Stack<Integer> backPointStack;

    public TokenStream(ArrayList<Token> tokenList) {
        this.tokenList = tokenList;
        this.readPoint = 0;
        this.backPointStack = new Stack<>();
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
        this.backPointStack.push(this.readPoint);
    }

    public void GoToBackPoint() {
        this.readPoint = this.backPointStack.pop();
    }
}
