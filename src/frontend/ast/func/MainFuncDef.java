package frontend.ast.func;

import error.Error;
import error.ErrorRecorder;
import error.ErrorType;
import frontend.ast.block.Block;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;
import midend.symbol.SymbolManger;

public class MainFuncDef extends Node {
    public MainFuncDef() {
        super(SyntaxType.MAIN_FUNC_DEF);
    }

    @Override
    public void Parse() {
        // 只需要识别，不需要判断对错
        // int
        this.AddNode(new TokenNode());
        // main
        this.AddNode(new TokenNode());
        // (
        this.AddNode(new TokenNode());
        // )
        if (GetCurrentTokenType().equals(TokenType.RPARENT)) {
            this.AddNode(new TokenNode());
        } else {
            this.AddMissRParentError();
        }
        // Block
        this.AddNode(new Block());
    }

    @Override
    public void Visit() {
        for (Node component : this.components) {
            if (component instanceof Block block) {
                SymbolManger.CreateSonSymbolTable();
                block.Visit();

                // 检查是否有正确的return
                this.CheckReturnError(block);

                SymbolManger.GoToFatherSymbolTable();
            }
        }
    }

    // 检查是否有正确的return
    private void CheckReturnError(Block block) {
        if (block.LastIsNotReturnStmt()) {
            ErrorRecorder.AddError(new Error(ErrorType.MISS_RETURN, block.GetLastLine()));
        }
    }

    public Block GetBlock() {
        for (Node node : this.components) {
            if (node instanceof Block block) {
                return block;
            }
        }
        throw new RuntimeException("no block in main func");
    }
}
