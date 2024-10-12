package frontend.ast.block;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

public class Block extends Node {
    public Block() {
        super(SyntaxType.BLOCK);
    }

    @Override
    public void Parse() {
        // {
        this.AddNode(new TokenNode());
        // BlockItem
        while (GetCurrentTokenType().equals(TokenType.RBRACE)) {
            this.AddNode(new BlockItem());
        }
        // }
        this.AddNode(new TokenNode());
    }
}
