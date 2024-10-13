package frontend.ast.decl;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.lexer.TokenType;

public class Decl extends Node {
    public Decl() {
        super(SyntaxType.DECL);
        this.printSelf = false;
    }

    @Override
    public void Parse() {
        this.AddNode(GetCurrentTokenType().equals(TokenType.CONSTTK) ?
            new ConstDecl() : new VarDecl());
    }
}
