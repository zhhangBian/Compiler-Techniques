package midend.visit;

import frontend.ast.block.Block;
import frontend.ast.block.BlockItem;

import java.util.ArrayList;

public class VisitorBlock {
    public static void VisitBlock(Block block) {
        ArrayList<BlockItem> blockItemList = block.GetBlockItemList();
        for (BlockItem blockItem : blockItemList) {
            VisitBlockItem(blockItem);
        }
    }

    public static void VisitBlockItem(BlockItem blockItem) {
        if (blockItem.IsDecl()) {
            VisitorDecl.VisitDecl(blockItem.GetDecl());
        } else {
            VisitorStmt.VisitStmt(blockItem.GetStmt());
        }
    }
}
