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
        Node node;
        // Ident
        node = new Ident();
        node.Parse();
        this.components.add(node);

        // 如果为数组：预留多维情况
        while (GetCurrentTokenType().equals(TokenType.LBRACK)) {
            // [
            node = new TokenNode();
            node.Parse();
            this.components.add(node);
            // ConstExp
            node = new ConstExp();
            node.Parse();
            this.components.add(node);
            // ]
            node = new TokenNode();
            node.Parse();
            this.components.add(node);
            // TODO：错误处理不匹配
        }

        // 如果为赋值语句
        if (GetCurrentTokenType().equals(TokenType.ASSIGN)) {
            // =
            node = new TokenNode();
            node.Parse();
            this.components.add(node);
            // InitVal
            node = new InitVal();
            node.Parse();
            this.components.add(node);
        }
    }
}
