package frontend.ast.decl;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.ConstExp;
import frontend.ast.value.ConstInitVal;
import frontend.ast.token.Ident;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;
import midend.symbol.Symbol;
import midend.symbol.SymbolManger;
import midend.symbol.SymbolType;
import midend.symbol.ValueSymbol;

import java.util.ArrayList;

public class ConstDef extends Node {
    private String type;
    private ValueSymbol symbol;

    public ConstDef() {
        super(SyntaxType.CONST_DEF);
    }

    public void SetTypeString(String type) {
        this.type = type;
    }

    public Symbol GetSymbol() {
        return this.symbol;
    }

    @Override
    public void Parse() {
        // Ident
        this.AddNode(new Ident());

        // 如果为数组：预留多维情况
        while (GetCurrentTokenType().equals(TokenType.LBRACK)) {
            // [
            this.AddNode(new TokenNode());
            // ConstExp
            this.AddNode(new ConstExp());
            // ]
            if (GetCurrentTokenType().equals(TokenType.RBRACK)) {
                this.AddNode(new TokenNode());
            } else {
                this.AddMissRBrackError();
            }
        }

        // 赋值语句
        if (GetCurrentTokenType().equals(TokenType.ASSIGN)) {
            // =
            this.AddNode(new TokenNode());
            // ConstInitVal
            this.AddNode(new ConstInitVal());
        }
    }

    @Override
    public void CreateSymbol() {
        Ident ident = (Ident) this.components.get(0);
        String symbolName = ident.GetTokenString();
        int line = ident.GetLine();

        int dimension = 0;
        ArrayList<Integer> depthList = new ArrayList<>();
        // 获取维度：判断是否有维度信息
        for (Node component : this.components) {
            if (component instanceof ConstExp) {
                dimension++;
            }
            // TODO：计算维度信息
            int depth = 1;
            depthList.add(depth);
        }
        // TODO：初始值的处理
        int constValue = 0;

        SymbolType type = SymbolType.GetConstType(this.type, dimension);
        this.symbol = new ValueSymbol(symbolName, type, dimension, depthList, constValue);
        SymbolManger.AddSymbol(this.symbol, line);
    }
}
