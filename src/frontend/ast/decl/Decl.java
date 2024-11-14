package frontend.ast.decl;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.lexer.TokenType;

public class Decl extends Node {
    private boolean isConstDecl;

    public Decl() {
        super(SyntaxType.DECL);
        this.printSelf = false;

        this.isConstDecl = false;
    }

    public boolean IsConstDecl() {
        return this.isConstDecl;
    }

    public Node GetDecl() {
        return this.components.get(0);
    }

    @Override
    public void Parse() {
        this.isConstDecl = GetCurrentTokenType().equals(TokenType.CONSTTK);
        this.AddNode(this.isConstDecl ? new ConstDecl() : new VarDecl());
    }
}
