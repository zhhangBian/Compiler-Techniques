package frontend.ast.exp;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.func.FuncRealParamS;
import frontend.ast.token.Ident;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

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
            }
            // TODO：错误处理
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
}
