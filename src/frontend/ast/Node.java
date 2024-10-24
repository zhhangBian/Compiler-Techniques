package frontend.ast;

import error.Error;
import error.ErrorRecorder;
import error.ErrorType;
import frontend.ast.token.TokenNode;
import frontend.lexer.Token;
import frontend.lexer.TokenStream;
import frontend.lexer.TokenType;

import java.util.ArrayList;

public abstract class Node {
    protected final SyntaxType syntaxType;
    protected final ArrayList<Node> components;
    protected boolean printSelf;

    private static final boolean FIX_ERROR = false;

    private static TokenStream tokenStream;

    public Node(SyntaxType syntaxType) {
        this.syntaxType = syntaxType;
        this.components = new ArrayList<>();
        this.printSelf = true;
    }

    public static void SetTokenStream(TokenStream tokenStream) {
        Node.tokenStream = tokenStream;
    }

    protected void AddNode(Node node) {
        node.Parse();
        this.components.add(node);
    }

    public SyntaxType GetSyntaxType() {
        return this.syntaxType;
    }

    public ArrayList<Node> GetComponents() {
        return this.components;
    }

    public abstract void Parse();

    public void Visit() {
        for (Node component : this.components) {
            component.Visit();
        }
    }

    public static Token GetCurrentToken() {
        return tokenStream.Peek(0);
    }

    public static TokenType GetCurrentTokenType() {
        return tokenStream.Peek(0).GetTokenType();
    }

    public static int GetBeforeLineNumber() {
        return Peek(-1).GetLineNumber();
    }

    public static int GetCurrentLineNumber() {
        return GetCurrentToken().GetLineNumber();
    }

    public static void Read() {
        tokenStream.Read();
    }

    public static Token Peek(int peekStep) {
        return tokenStream.Peek(peekStep);
    }

    public static void SetBackPoint() {
        tokenStream.SetBackPoint();
    }

    public static void GoToBackPoint() {
        tokenStream.GoToBackPoint();
    }

    protected void AddMissSemicnError() {
        int line = GetBeforeLineNumber();
        ErrorRecorder.AddError(new Error(ErrorType.MISS_SEMICN, line));

        if (FIX_ERROR) {
            this.components.add(new TokenNode(new Token(TokenType.SEMICN, ";", line)));
        }
    }

    protected void AddMissRParentError() {
        int line = GetBeforeLineNumber();
        ErrorRecorder.AddError(new Error(ErrorType.MISS_RPARENT, line));

        if (FIX_ERROR) {
            this.components.add(new TokenNode(new Token(TokenType.RPARENT, ")", line)));
        }
    }

    protected void AddMissRBrackError() {
        int line = GetBeforeLineNumber();
        ErrorRecorder.AddError(new Error(ErrorType.MISS_RBRACK, line));

        if (FIX_ERROR) {
            this.components.add(new TokenNode(new Token(TokenType.RBRACK, "]", line)));
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Node node : this.components) {
            stringBuilder.append(node);
            stringBuilder.append("\n");
        }

        if (printSelf) {
            stringBuilder.append("<" + this.syntaxType + ">");
        } else {
            if (!stringBuilder.isEmpty() &&
                stringBuilder.charAt(stringBuilder.length() - 1) == '\n') {
                stringBuilder.setLength(stringBuilder.length() - 1);
            }
        }

        return stringBuilder.toString();
    }

    public String GetSimpleName() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Node node : this.components) {
            stringBuilder.append(node.GetSimpleName());
        }

        return stringBuilder.toString();
    }
}
