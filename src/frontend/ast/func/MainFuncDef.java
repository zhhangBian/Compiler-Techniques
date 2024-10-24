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
        Block block = (Block) this.components.get(4);
        SymbolManger.GoToSonSymbolTable();
        block.Visit();
        if (!block.LastIsReturnStmt()) {
            ErrorRecorder.AddError(new Error(ErrorType.MISS_RETURN, block.GetLastLine()));
        }
        SymbolManger.GoToFatherSymbolTable();
    }
}
