package frontend.parser;

import frontend.ast.CompUnit;
import frontend.ast.Node;
import frontend.lexer.Token;
import frontend.lexer.TokenStream;
import frontend.lexer.TokenType;

import java.util.ArrayList;

public class Parser {
    private TokenStream tokenStream;
    private Node rootNode;

    public Parser() {
        this.tokenStream = null;
        this.rootNode = null;
    }

    public void SetTokenStream(TokenStream tokenStream) {
        this.tokenStream = tokenStream;
        this.tokenStream.Read();
    }

    public void GenerateAstTree() {
        Node.tokenStream = this.tokenStream;
        this.rootNode = new CompUnit(this.tokenStream);
        this.rootNode.Parse();
    }

    public Node GetAstTree() {
        return this.rootNode;
    }
}
