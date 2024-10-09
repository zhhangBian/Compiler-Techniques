package frontend.parser;

import frontend.ast.Node;
import frontend.lexer.Token;
import frontend.lexer.TokenStream;

public class Parser {
    private TokenStream tokenStream;
    private Token currentToken;
    private Node rootNode;

    public Parser() {
        this.tokenStream = null;
        this.currentToken = null;
        this.rootNode = null;
    }

    public void SetTokenStream(TokenStream tokenStream) {
        this.tokenStream = tokenStream;
    }

    // 将函数视作main的孩子结点
    public void GenerateAstTree() {
        // TODO
        this.rootNode = null;
    }

    // 返回AST树的根节点
    public Node GetAstTree() {
        return this.rootNode;
    }

    private void Read() {
        this.currentToken = this.tokenStream.Read();
    }

    private void UnRead() {
        this.tokenStream.UnRead();
    }
}
