package frontend.ast.decl;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.ConstExp;
import frontend.ast.value.InitVal;
import frontend.ast.token.Ident;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;
import midend.symbol.SymbolManger;
import midend.symbol.SymbolType;
import midend.symbol.ValueSymbol;

import java.util.ArrayList;

public class VarDef extends Node {
    private String type;

    public VarDef() {
        super(SyntaxType.VAR_DEF);
    }

    public void SetTypeString(String type) {
        this.type = type;
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
    public void GenerateIr() {
        String symbolName = ((Ident) this.components.get(0)).GetTokenString();
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

        SymbolType type = (dimension == 0) ? SymbolType.GetVarType(this.type) :
            SymbolType.GetVarArrayType(this.type);
        SymbolManger.AddSymbol(new ValueSymbol(symbolName, type, dimension, depthList));
    }
}
