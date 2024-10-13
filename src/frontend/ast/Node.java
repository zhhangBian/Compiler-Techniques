package frontend.ast;

import frontend.lexer.Token;
import frontend.lexer.TokenStream;
import frontend.lexer.TokenType;

import java.util.ArrayList;

public abstract class Node {
    protected final SyntaxType syntaxType;
    protected final ArrayList<Node> components;
    protected boolean printSelf;

    public static TokenStream tokenStream;

    public Node(SyntaxType syntaxType) {
        this.syntaxType = syntaxType;
        this.components = new ArrayList<>();
        this.printSelf = true;
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

    public static void SetBackPoint() {
        tokenStream.SetBackPoint();
    }

    public static void GoToBackPoint() {
        tokenStream.GoToBackPoint();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Node node : this.components) {
            stringBuilder.append(node);
            stringBuilder.append("\n");
        }

        if (printSelf) {
            stringBuilder.append("<" + this.syntaxType + ">");
        } else {
            if (!stringBuilder.isEmpty() &&
                stringBuilder.charAt(stringBuilder.length() - 1) == '\n') {
                stringBuilder.setLength(stringBuilder.length() - 1);
            }
        }

        return stringBuilder.toString();
    }
}
