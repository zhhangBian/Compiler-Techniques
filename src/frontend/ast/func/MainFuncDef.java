package frontend.ast.func;

import error.Error;
import error.ErrorRecorder;
import error.ErrorType;
import frontend.ast.block.Block;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;
import midend.llvm.IrBuilder;
import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrFunction;
import midend.symbol.SymbolManger;
import utils.Setting;

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
                // 创建函数IR
                IrFunction irFunction = IrBuilder.GetFunctionIr("main", IrBaseType.INT32);
                IrBuilder.SetCurrentFunction(irFunction);
                IrBuilder.GetNewBasicBlock();

                SymbolManger.GoToSonSymbolTable();
                block.Visit();

                if (Setting.CHECK_ERROR) {
                    this.CheckReturnError(block);
                }

                SymbolManger.GoToFatherSymbolTable();
            }
        }
    }

    private void CheckReturnError(Block block) {
        if (!block.LastIsReturnStmt()) {
            ErrorRecorder.AddError(new Error(ErrorType.MISS_RETURN, block.GetLastLine()));
        }
    }
}
