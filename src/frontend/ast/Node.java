package frontend.ast;

import java.util.ArrayList;

public class Node {
    private final SyntaxType syntaxType;
    private final ArrayList<Node> components;

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
}
