package frontend.ast;

import frontend.lexer.Token;
import frontend.lexer.TokenStream;

import java.util.ArrayList;

public abstract class Node {
    private final SyntaxType syntaxType;
    protected final ArrayList<Node> components;

    public static TokenStream tokenStream;

    public Node(SyntaxType syntaxType, ArrayList<Node> components) {
        this.syntaxType = syntaxType;
        this.components = components;
    }

    public SyntaxType GetSyntaxType() {
        return this.syntaxType;
    }

    public ArrayList<Node> GetComponents() {
        return this.components;
    }

    public abstract void Parse();

    public static Token GetCurrentToken() {
        return tokenStream.Peek(0);
    }

    public static Token Read() {
        return tokenStream.Read();
    }

    public static Token Peek(int peekStep) {
        return tokenStream.Peek(peekStep);
    }
}
