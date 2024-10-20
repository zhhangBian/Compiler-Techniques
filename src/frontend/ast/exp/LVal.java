package frontend.ast.exp;

import error.Error;
import error.ErrorRecorder;
import error.ErrorType;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.Ident;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;
import midend.symbol.SymbolManger;

public class LVal extends Node {
    public LVal() {
        super(SyntaxType.LVAL_EXP);
    }

    @Override
    public void Parse() {
        // Ident
        this.AddNode(new Ident());
        // 数组形式
        if (GetCurrentTokenType().equals(TokenType.LBRACK)) {
            // [
            this.AddNode(new TokenNode());
            // Exp
            this.AddNode(new Exp());
            // ]
            if (GetCurrentTokenType().equals(TokenType.RBRACK)) {
                this.AddNode(new TokenNode());
            } else {
                this.AddMissRBrackError();
            }
        }
    }

    @Override
    public void CheckError() {
        for (Node component : this.components) {
            if (component instanceof Ident ident) {
                String identName = ident.GetTokenString();
                // 未定义
                if (SymbolManger.GetSymbol(identName) == null) {
                    ErrorRecorder.AddError(new Error(ErrorType.NAME_UNDEFINED, ident.GetLine()));
                }
            }
        }
    }
}
