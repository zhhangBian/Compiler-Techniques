package frontend.ast.decl;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.lexer.TokenType;

import java.util.ArrayList;

public class Decl extends Node {
    public Decl() {
        super(SyntaxType.DECL, new ArrayList<>());
    }

    @Override
    public void Parse() {
        Node node;
        if (GetCurrentToken().GetTokenType().equals(TokenType.CONSTTK)) {
            node = new ConstDecl();
        } else {
            node = new VarDecl();
        }
        node.Parse();
        this.components.add(node);
    }
}
