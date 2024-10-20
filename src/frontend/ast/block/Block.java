package frontend.ast.block;

import error.Error;
import error.ErrorRecorder;
import error.ErrorType;
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
        while (!GetCurrentTokenType().equals(TokenType.RBRACE)) {
            this.AddNode(new BlockItem());
        }
        // }
        this.AddNode(new TokenNode());
    }

    public boolean LastIsReturnStmt() {
        if (this.components.size() == 2) {
            return false;
        }

        BlockItem lastBlockItem = (BlockItem) this.components.get(this.components.size() - 2);
        return lastBlockItem.IsReturnStmt();
    }

    public int GetLastLine() {
        TokenNode tokenNode = (TokenNode) this.components.get(this.components.size() - 1);
        return tokenNode.GetLine();
    }
}
