package frontend.ast.decl;

import frontend.ast.token.BType;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

public class VarDecl extends Node {
    public VarDecl() {
        super(SyntaxType.VAR_DECL);
    }

    @Override
    public void Parse() {
        Node node;
        // BType
        node = new BType();
        node.Parse();
        this.components.add(node);
        // VarDef
        node = new VarDef();
        node.Parse();
        this.components.add(node);

        // 重复的constDef
        while (GetCurrentTokenType().equals(TokenType.COMMA)) {
            // ,
            node = new TokenNode();
            node.Parse();
            this.components.add(node);
            // ConstDef
            node = new VarDef();
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
