package frontend.ast;

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
}
