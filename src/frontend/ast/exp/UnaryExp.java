package frontend.ast.exp;

import error.Error;
import error.ErrorRecorder;
import error.ErrorType;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.func.FuncRealParamS;
import frontend.ast.token.Ident;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;
import midend.symbol.FuncSymbol;
import midend.symbol.Symbol;
import midend.symbol.SymbolManger;

public class UnaryExp extends Node {
    public UnaryExp() {
        super(SyntaxType.UNARY_EXP);
    }

    @Override
    public void Parse() {
        // 函数调用
        if (GetCurrentTokenType().equals(TokenType.IDENFR) &&
            Peek(1).GetTokenType().equals(TokenType.LPARENT)) {
            // Ident
            this.AddNode(new Ident());
            // (
            this.AddNode(new TokenNode());
            // 函数参数
            if (GetCurrentTokenType().equals(TokenType.INTCON) ||
                GetCurrentTokenType().equals(TokenType.CHRCON) ||
                GetCurrentTokenType().equals(TokenType.IDENFR) ||
                GetCurrentTokenType().equals(TokenType.PLUS) ||
                GetCurrentTokenType().equals(TokenType.MINU) ||
                GetCurrentTokenType().equals(TokenType.NOT) ||
                GetCurrentTokenType().equals(TokenType.LPARENT)) {
                this.AddNode(new FuncRealParamS());
            }
            // )
            if (GetCurrentTokenType().equals(TokenType.RPARENT)) {
                this.AddNode(new TokenNode());
            } else {
                this.AddMissRParentError();
            }
        }
        // 带符号的表达式（右递归）
        else if (GetCurrentTokenType().equals(TokenType.PLUS) ||
            GetCurrentTokenType().equals(TokenType.MINU) ||
            GetCurrentTokenType().equals(TokenType.NOT)) {
            //  UnaryOp
            this.AddNode(new UnaryOp());
            //  UnaryExp
            this.AddNode(new UnaryExp());
        }
        // 基本表达式
        else {
            this.AddNode(new PrimaryExp());
        }
    }

    @Override
    public void CheckError() {
        for (Node component : this.components) {
            if (component instanceof Ident ident) {
                String identName = ident.GetTokenString();
                int line = ident.GetLine();

                Symbol symbol = SymbolManger.GetSymbol(identName);
                if (symbol == null) {
                    ErrorRecorder.AddError(new Error(ErrorType.NAME_UNDEFINED, line));
                    return;
                }

                if (symbol instanceof FuncSymbol funcSymbol) {
                    FuncRealParamS funcRealParamS = this.GetFuncRealParaS();
                    if (funcRealParamS != null) {
                        int realParamCount = funcRealParamS.GetRealParamCount();
                        int formalParamCount = funcSymbol.GetFormalParamList().size();
                        if (realParamCount != formalParamCount) {
                            ErrorRecorder.AddError(
                                new Error(ErrorType.FUNC_PARAM_NUM_NOT_MATCH, line));
                        }
                    } else {
                        ErrorRecorder.AddError(new Error(ErrorType.UNDEFINED, line));
                    }
                }
                // 相应的符号不是函数
                else {
                    ErrorRecorder.AddError(new Error(ErrorType.UNDEFINED, line));
                }
            }
        }
    }

    private FuncRealParamS GetFuncRealParaS() {
        for (Node component : this.components) {
            if (component instanceof FuncRealParamS funcRealParamS) {
                return funcRealParamS;
            }
        }
        return null;
    }
}
