package frontend.ast.func;

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
    public void CreateSymbol() {
        for (Node component : this.components) {
            if (component instanceof Block) {
                SymbolManger.GoToSonSymbolTable();
                component.CreateSymbol();
                SymbolManger.GoToFatherSymbolTable();
            }
        }
    }
}
