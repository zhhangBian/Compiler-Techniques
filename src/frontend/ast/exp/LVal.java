package frontend.ast.exp;

import error.Error;
import error.ErrorRecorder;
import error.ErrorType;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.Ident;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;
import midend.symbol.Symbol;
import midend.symbol.SymbolManger;
import midend.symbol.SymbolType;
import utils.Setting;

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
    public void Visit() {
        for (Node component : this.components) {
            if (Setting.CHECK_ERROR) {
                this.CheckSymbolError(component);
            }

            component.Visit();
        }
    }

    private void CheckSymbolError(Node component) {
        if (!(component instanceof Ident ident)) {
            return;
        }

        String identName = ident.GetTokenString();
        // 未定义
        if (SymbolManger.GetSymbol(identName) == null) {
            ErrorRecorder.AddError(new Error(ErrorType.NAME_UNDEFINED, ident.GetLine()));
        }
    }

    public boolean HaveSymbol() {
        String identName = ((Ident) this.components.get(0)).GetTokenString();
        return SymbolManger.GetSymbol(identName) != null;
    }

    public boolean CannotChangeValue() {
        String identName = ((Ident) this.components.get(0)).GetTokenString();
        Symbol symbol = SymbolManger.GetSymbol(identName);
        if (symbol == null) {
            return true;
        }

        SymbolType type = symbol.GetSymbolType();
        // 是变量
        if (this.components.size() == 1) {
            return !type.equals(SymbolType.INT) && !type.equals(SymbolType.CHAR);
        }
        // 是数组
        else {
            return !type.equals(SymbolType.INT_ARRAY) && !type.equals(SymbolType.CHAR_ARRAY);
        }
    }

    public int GetLine() {
        return ((Ident) this.components.get(0)).GetLine();
    }
}
