package frontend.ast.func;

import error.Error;
import error.ErrorRecorder;
import error.ErrorType;
import frontend.ast.block.Block;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.FuncType;
import frontend.ast.token.Ident;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;
import midend.symbol.FuncSymbol;
import midend.symbol.SymbolManger;
import midend.symbol.SymbolType;

public class FuncDef extends Node {
    private FuncSymbol symbol;

    public FuncDef() {
        super(SyntaxType.FUNC_DEF);
    }

    public FuncSymbol GetSymbol() {
        return this.symbol;
    }

    @Override
    public void Parse() {
        // FuncType
        FuncType funcType = new FuncType();
        funcType.Parse();
        this.components.add(funcType);
        // Ident
        this.AddNode(new Ident());
        // (
        this.AddNode(new TokenNode());
        // FuncFParams
        if (GetCurrentTokenType().equals(TokenType.INTTK) ||
            GetCurrentTokenType().equals(TokenType.CHARTK)) {
            this.AddNode(new FuncFormalParamS());
        }
        // )
        if (GetCurrentTokenType().equals(TokenType.RPARENT)) {
            this.AddNode(new TokenNode());
        } else {
            this.AddMissRParentError();
        }
        // Block
        SymbolManger.EnterFunc(funcType.GetTokenString());
        this.AddNode(new Block());
        SymbolManger.LeaveFunc();
    }

    @Override
    public void Visit() {
        // FuncType
        String type = ((FuncType) this.components.get(0)).GetTokenString();
        // Ident
        Ident ident = (Ident) this.components.get(1);
        String symbolName = ident.GetTokenString();
        int line = ident.GetLine();
        this.symbol = new FuncSymbol(symbolName, SymbolType.GetFuncType(type));
        SymbolManger.AddSymbol(this.symbol, line);

        // 解析形参和函数体的符号
        SymbolManger.CreateSonSymbolTable();
        for (Node component : this.components) {
            component.Visit();
            if (component instanceof FuncFormalParamS funcFormalParamS) {
                this.symbol.SetFormalParamList(funcFormalParamS.GetFormalParamList());
            }
            // 检查是否有正确的return
            this.CheckReturnError(type, component);
        }
        SymbolManger.GoToFatherSymbolTable();
    }

    // 检查是否有正确的return
    private void CheckReturnError(String type, Node component) {
        if (!(component instanceof Block block)) {
            return;
        }

        if (type.equals("int") || type.equals("char")) {
            if (block.LastIsNotReturnStmt()) {
                ErrorRecorder.AddError(new Error(ErrorType.MISS_RETURN, block.GetLastLine()));
            }
        }
    }

    public Block GetBlock() {
        for (Node node : this.components) {
            if (node instanceof Block block) {
                return block;
            }
        }
        throw new RuntimeException("no block in this func");
    }
}
