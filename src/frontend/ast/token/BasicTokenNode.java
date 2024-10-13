package frontend.ast.token;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.lexer.Token;

import static utils.Debugger.DebugPrint;

public class BasicTokenNode extends Node {
    public BasicTokenNode(SyntaxType syntaxType) {
        super(syntaxType);
    }

    protected Token token;

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
}
