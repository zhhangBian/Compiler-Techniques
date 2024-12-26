package frontend.ast.block;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

import java.util.ArrayList;

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

    public boolean LastIsNotReturnStmt() {
        if (this.components.size() == 2) {
            return true;
        }

        BlockItem lastBlockItem = (BlockItem) this.components.get(this.components.size() - 2);
        return !lastBlockItem.IsReturnStmt();
    }

    public int GetLastLine() {
        TokenNode tokenNode = (TokenNode) this.components.get(this.components.size() - 1);
        return tokenNode.GetLine();
    }

    public ArrayList<BlockItem> GetBlockItemList() {
        ArrayList<BlockItem> blockItemList = new ArrayList<>();
        for (Node node : this.components) {
            if (node instanceof BlockItem blockItem) {
                blockItemList.add(blockItem);
            }
        }
        return blockItemList;
    }
}
