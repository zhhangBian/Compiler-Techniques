package frontend.ast;

import frontend.lexer.Token;
import frontend.lexer.TokenStream;
import frontend.lexer.TokenType;

import java.util.ArrayList;

public abstract class Node {
    private final SyntaxType syntaxType;
    protected final ArrayList<Node> components;

    public static TokenStream tokenStream;

    public Node(SyntaxType syntaxType) {
        this.syntaxType = syntaxType;
        this.components = new ArrayList<>();
    }

    protected void AddNode(Node node) {
        node.Parse();
        this.components.add(node);
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

    public static TokenType GetCurrentTokenType() {
        return tokenStream.Peek(0).GetTokenType();
    }

    public static void Read() {
        tokenStream.Read();
    }

    public static Token Peek(int peekStep) {
        return tokenStream.Peek(peekStep);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Node node : this.components) {
            stringBuilder.append(node);
            stringBuilder.append("\n");
        }
        stringBuilder.append(this.syntaxType);

        return stringBuilder.toString();
    }
}
