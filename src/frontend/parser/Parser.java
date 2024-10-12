package frontend.parser;

import frontend.ast.CompUnit;
import frontend.ast.Node;
import frontend.lexer.TokenStream;

public class Parser {
    private TokenStream tokenStream;
    private Node rootNode;

    public Parser() {
        this.tokenStream = null;
        this.rootNode = null;
    }

    public void SetTokenStream(TokenStream tokenStream) {
        this.tokenStream = tokenStream;
    }

    public void GenerateAstTree() {
        Node.tokenStream = this.tokenStream;
        this.rootNode = new CompUnit();
        this.rootNode.Parse();
    }

    public Node GetAstTree() {
        return this.rootNode;
    }
}
