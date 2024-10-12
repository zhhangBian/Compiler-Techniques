package frontend.ast.decl;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.lexer.TokenType;

public class Decl extends Node {
    public Decl() {
        super(SyntaxType.DECL);
    }

    @Override
    public void Parse() {
        Node node = GetCurrentTokenType().equals(TokenType.CONSTTK) ?
            new ConstDecl() : new VarDecl();
        node.Parse();
        this.components.add(node);
    }
}
