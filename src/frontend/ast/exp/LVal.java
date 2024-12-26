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
import midend.symbol.ValueSymbol;

import java.util.ArrayList;

public class LVal extends ComputeExp {
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
            // 是否是未定义符号
            this.CheckIdentUnDefinedError(component);

            component.Visit();
        }
    }

    private void CheckIdentUnDefinedError(Node component) {
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

    public boolean IsConstType() {
        String identName = ((Ident) this.components.get(0)).GetTokenString();
        Symbol symbol = SymbolManger.GetSymbol(identName);
        if (symbol == null) {
            return true;
        }

        SymbolType type = symbol.GetSymbolType();
        return type.equals(SymbolType.CONST_INT) || type.equals(SymbolType.CONST_CHAR) ||
            type.equals(SymbolType.CONST_INT_ARRAY) || type.equals(SymbolType.CONST_CHAR_ARRAY);
    }

    public int GetLine() {
        return ((Ident) this.components.get(0)).GetLine();
    }

    public Ident GetIdent() {
        return (Ident) this.components.get(0);
    }

    public ArrayList<Exp> GetExpList() {
        ArrayList<Exp> expList = new ArrayList<>();
        for (Node node : this.components) {
            if (node instanceof Exp exp) {
                expList.add(exp);
            }
        }
        return expList;
    }

    @Override
    public void Compute() {
        Ident ident = this.GetIdent();
        String symbolName = ident.GetSimpleName();
        Symbol symbol = SymbolManger.GetSymbol(symbolName);

        if (symbol instanceof ValueSymbol valueSymbol) {
            if (valueSymbol.IsConst()) {
                // 如果不是数组
                if (this.components.size() == 1) {
                    this.isConst = true;
                    this.value = valueSymbol.GetInitValueList().get(0);
                }
                //如果是数组
                else {
                    Exp exp = (Exp) this.components.get(2);
                    exp.Compute();
                    if (exp.GetIfConst()) {
                        this.isConst = true;
                        int index = exp.GetValue();
                        // 字符串数组越界的情况
                        this.value = index >= valueSymbol.GetValueList().size() ?
                            0 : valueSymbol.GetInitValueList().get(exp.GetValue());
                    }
                }
            }
        }
    }
}
