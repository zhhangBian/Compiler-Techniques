package frontend.ast.block;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.decl.Decl;
import frontend.ast.stmt.Stmt;
import frontend.lexer.TokenType;

public class BlockItem extends Node {
    public BlockItem() {
        super(SyntaxType.BLOCK_ITEM);
        this.printSelf = false;
    }

    @Override
    public void Parse() {
        if (GetCurrentTokenType().equals(TokenType.CONSTTK) ||
            GetCurrentTokenType().equals(TokenType.INTTK) ||
            GetCurrentTokenType().equals(TokenType.CHARTK)) {
            this.AddNode(new Decl());
        } else {
            this.AddNode(new Stmt());
        }
    }

    public boolean IsReturnStmt() {
        Node component = this.components.get(0);
        if (component instanceof Stmt stmt) {
            return stmt.IsReturnStmt();
        }
        return false;
    }

    public boolean IsDecl() {
        return this.components.get(0) instanceof Decl;
    }

    public Decl GetDecl() {
        return (Decl) this.components.get(0);
    }

    public Stmt GetStmt() {
        return (Stmt) this.components.get(0);
    }
}
