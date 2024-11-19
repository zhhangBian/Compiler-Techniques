package frontend.ast.exp;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.TokenNode;
import frontend.ast.value.Char;
import frontend.ast.value.Number;
import frontend.lexer.TokenType;

public class PrimaryExp extends ComputeExp {
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
            if (GetCurrentTokenType().equals(TokenType.RPARENT)) {
                this.AddNode(new TokenNode());
            } else {
                this.AddMissRParentError();
            }
        }
        // Number
        else if (GetCurrentTokenType().equals(TokenType.INTCON)) {
            this.AddNode(new Number());
        }
        // Character
        else if (GetCurrentTokenType().equals(TokenType.CHRCON)) {
            this.AddNode(new Char());
        }
        // LVal
        else {
            this.AddNode(new LVal());
        }
    }

    @Override
    public void Compute() {
        Node node = this.components.get(0);
        if (node instanceof Number number) {
            this.isConst = true;
            this.value = number.GetValue();
        } else if (node instanceof Char character) {
            this.isConst = true;
            this.value = character.GetValue();
        } else if (node instanceof TokenNode) {
            Exp exp = (Exp) this.components.get(1);
            exp.Compute();
            this.isConst = exp.GetIfConst();
            this.value = exp.GetValue();
        } else if (node instanceof LVal lVal) {
            lVal.Compute();
            this.isConst = lVal.GetIfConst();
            this.value = lVal.GetValue();
        }
    }

    public boolean IsExpType() {
        return this.components.get(0) instanceof TokenNode;
    }

    public Exp GetExp() {
        return (Exp) this.components.get(1);
    }

    public boolean IsLValType() {
        return this.components.get(0) instanceof LVal;
    }

    public LVal GetLVal() {
        return (LVal) this.components.get(0);
    }

    public boolean IsNumberType() {
        return this.components.get(0) instanceof Number;
    }

    public Number GetNumber() {
        return (Number) this.components.get(0);
    }

    public boolean IsCharacterType() {
        return this.components.get(0) instanceof Char;
    }

    public Char GetCharacter() {
        return (Char) this.components.get(0);
    }
}
