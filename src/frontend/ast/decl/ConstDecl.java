package frontend.ast.decl;

import frontend.ast.token.BType;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

public class ConstDecl extends Node {
    public ConstDecl() {
        super(SyntaxType.CONST_DECL);
    }

    @Override
    public void Parse() {
        Node node;
        // 得到const
        node = new TokenNode();
        node.Parse();
        this.components.add(node);
        // BType
        node = new BType();
        node.Parse();
        this.components.add(node);
        // ConstDef
        node = new ConstDef();
        node.Parse();
        this.components.add(node);

        // 重复的constDef
        while (GetCurrentTokenType().equals(TokenType.COMMA)) {
            // ,
            node = new TokenNode();
            node.Parse();
            this.components.add(node);
            // ConstDef
            node = new ConstDef();
            node.Parse();
            this.components.add(node);
        }

        // ;
        node = new TokenNode();
        node.Parse();
        this.components.add(node);
        // TODO：错误处理
    }
}
