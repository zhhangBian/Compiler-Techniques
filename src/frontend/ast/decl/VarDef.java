package frontend.ast.decl;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.ConstExp;
import frontend.ast.value.InitVal;
import frontend.ast.token.Ident;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

public class VarDef extends Node {
    public VarDef() {
        super(SyntaxType.VAR_DEF);
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
            this.AddNode(new TokenNode());
            // TODO：错误处理不匹配
        }

        // 如果为赋值语句
        if (GetCurrentTokenType().equals(TokenType.ASSIGN)) {
            // =
            this.AddNode(new TokenNode());
            // InitVal
            this.AddNode(new InitVal());
        }
    }
}
