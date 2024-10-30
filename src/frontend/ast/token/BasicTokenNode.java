package frontend.ast.token;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.lexer.Token;

import static utils.Debug.DebugPrint;

public class BasicTokenNode extends Node {
    public BasicTokenNode(SyntaxType syntaxType) {
        super(syntaxType);
    }

    protected Token token;

    public String GetTokenString() {
        return this.token.GetStringValue();
    }

    public int GetLine() {
        return this.token.GetLineNumber();
    }

    @Override
    public void Parse() {
        this.token = GetCurrentToken();
        Read();

        DebugPrint(this.token);
    }

    @Override
    public String toString() {
        return this.token.GetTokenType() + " " + this.token.GetStringValue();
    }

    @Override
    public String GetSimpleName() {
        return this.token.GetStringValue();
    }
}
