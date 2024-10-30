package frontend.ast.func;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.BType;
import frontend.ast.token.Ident;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;
import midend.symbol.Symbol;
import midend.symbol.SymbolManger;
import midend.symbol.SymbolType;
import midend.symbol.ValueSymbol;

import java.util.ArrayList;

public class FuncFormalParam extends Node {
    private Symbol symbol;

    public FuncFormalParam() {
        super(SyntaxType.FUNC_FORMAL_PARAM);
    }

    public Symbol GetSymbol() {
        return this.symbol;
    }

    @Override
    public void Parse() {
        this.AddNode(new BType());
        this.AddNode(new Ident());
        // 数组形，预留多维的形式
        while (GetCurrentTokenType().equals(TokenType.LBRACK)) {
            this.AddNode(new TokenNode());
            if (GetCurrentTokenType().equals(TokenType.RBRACK)) {
                this.AddNode(new TokenNode());
            } else {
                this.AddMissRBrackError();
            }
        }
    }

    @Override
    public void Visit() {
        super.Visit();
        String type = ((BType) this.components.get(0)).GetTokenString();
        Ident ident = (Ident) this.components.get(1);
        String symbolName = ident.GetTokenString();
        // 为数组类型
        if (this.components.size() >= 3) {
            int dimension = 0;
            for (Node component : this.components) {
                if (component instanceof TokenNode) {
                    if (((TokenNode) component).GetTokenString().equals("[")) {
                        dimension++;
                    }
                }
            }
            this.symbol = new ValueSymbol(
                symbolName, SymbolType.GetVarType(type, dimension),
                dimension, new ArrayList<>());
        } else {
            this.symbol = new ValueSymbol(symbolName, SymbolType.GetVarType(type));
        }

        int line = ((Ident) this.components.get(1)).GetLine();
        SymbolManger.AddSymbol(this.symbol, line);
    }
}
