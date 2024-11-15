package frontend.ast.decl;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.ConstExp;
import frontend.ast.value.InitVal;
import frontend.ast.token.Ident;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;
import midend.symbol.Symbol;
import midend.symbol.SymbolManger;
import midend.symbol.SymbolType;
import midend.symbol.ValueSymbol;

import java.util.ArrayList;

public class VarDef extends Node {
    private String type;
    private ValueSymbol symbol;

    public VarDef() {
        super(SyntaxType.VAR_DEF);
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

        // 如果为赋值语句
        if (GetCurrentTokenType().equals(TokenType.ASSIGN)) {
            // =
            this.AddNode(new TokenNode());
            // InitVal
            this.AddNode(new InitVal());
        }
    }

    @Override
    public void Visit() {
        Ident ident = (Ident) this.components.get(0);
        String symbolName = ident.GetTokenString();
        int line = ident.GetLine();

        int dimension = 0;
        ArrayList<Integer> depthList = new ArrayList<>();
        ArrayList<Integer> initValueList = null;
        // 获取维度：判断是否有维度信息
        for (Node component : this.components) {
            component.Visit();
            if (component instanceof ConstExp constExp) {
                dimension++;
                // 计算维度信息
                constExp.Compute();
                depthList.add(constExp.GetValue());
            } else if (component instanceof InitVal initVal) {
                initValueList = initVal.GetInitValueList();
            }
        }

        SymbolType type = SymbolType.GetVarType(this.type, dimension);

        this.symbol = SymbolManger.IsGlobal() ?
            new ValueSymbol(symbolName, type, dimension, depthList, initValueList) :
            new ValueSymbol(symbolName, type, dimension, depthList);
        SymbolManger.AddSymbol(symbol, line);
    }

    public Ident GetIdent() {
        return (Ident) this.components.get(0);
    }

    public boolean HaveInitVal() {
        for (Node node : this.components) {
            if (node instanceof InitVal) {
                return true;
            }
        }
        return false;
    }

    public InitVal GetInitVal() {
        for (Node node : this.components) {
            if (node instanceof InitVal initVal) {
                return initVal;
            }
        }
        throw new RuntimeException("no init value");
    }
}
