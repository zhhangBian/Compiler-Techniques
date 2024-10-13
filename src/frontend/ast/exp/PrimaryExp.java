package frontend.ast.exp;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.TokenNode;
import frontend.ast.value.Character;
import frontend.ast.value.Number;
import frontend.lexer.TokenType;

public class PrimaryExp extends Node {
    public PrimaryExp() {
        super(SyntaxType.PRIMARY_EXP);
    }

    @Override
    public void Parse() {
        if (GetCurrentTokenType().equals(TokenType.LPARENT)) {
            // (
            this.AddNode(new TokenNode());
            // Exp
            this.AddNode(new Exp());
            // )
            this.AddNode(new TokenNode());
        }
        // Number
        else if (GetCurrentTokenType().equals(TokenType.INTCON)) {
            this.AddNode(new Number());
        }
        // Character
        else if (GetCurrentTokenType().equals(TokenType.CHRCON)) {
            this.AddNode(new Character());
        }
        // LVal
        else {
            this.AddNode(new LVal());
        }
    }
}
