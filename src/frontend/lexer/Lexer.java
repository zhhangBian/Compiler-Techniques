package frontend.lexer;

import error.Error;
import error.ErrorRecorder;
import error.ErrorType;

import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.ArrayList;

public class Lexer {
    private final PushbackInputStream reader;
    private final ArrayList<Token> tokenList;
    private char currentChar;
    private int lineNumber;

    public Lexer(PushbackInputStream reader) throws IOException {
        this.reader = reader;
        this.tokenList = new ArrayList<>();
        this.currentChar = (char) reader.read();
        this.lineNumber = 1;
    }

    public void GenerateTokenList() throws IOException {
        Token token = this.GetToken();
        while (!token.GetTokenType().equals(TokenType.EOF)) {
            this.tokenList.add(token);
            token = this.GetToken();
        }
    }

    public ArrayList<Token> GetTokenList() {
        return this.tokenList;
    }

    private void Read() throws IOException {
        this.currentChar = (char) reader.read();
    }

    private void UnRead() throws IOException {
        this.reader.unread(this.currentChar);
    }

    private Token GetToken() throws IOException {
        StringBuilder string = new StringBuilder();

        // 跳过空白字符
        this.SkipBlank();

        // 处理结束情况
        if (this.IsEof()) {
            return new Token(TokenType.EOF, "EOF", this.lineNumber);
        }
        // 数字常量
        else if (this.IsDigit()) {
            return this.LexerDigit(string);
        }
        // 字符串常量
        else if (this.IsStringConst()) {
            return this.LexerStringConst(string);
        }
        // 字符常量
        else if (this.IsCharacterConst()) {
            return this.LexerCharacterConst(string);
        }
        // 标识符
        else if (this.IsIdentifier()) {
            return this.LexerIdentifier(string);
        }
        // 处理注释
        else if (this.IsAnnotation()) {
            return this.LexerAnnotation(string);
        }
        // 处理一般的符号
        else {
            return this.LexerOp(string);
        }
    }

    private void SkipBlank() throws IOException {
        while (this.IsBlank()) {
            if (this.IsNewLine()) {
                this.lineNumber++;
            }
            this.Read();
        }
    }

    private boolean IsBlank() throws IOException {
        return this.currentChar == ' ' ||
            this.currentChar == '\t' ||
            this.IsNewLine();
    }

    private boolean IsNewLine() throws IOException {
        if (this.currentChar == '\r') {
            this.Read();
        }
        return this.currentChar == '\n';
    }

    private boolean IsEof() {
        return this.currentChar == '\uFFFF';
    }

    private boolean IsDigit() {
        return Character.isDigit(this.currentChar);
    }

    private boolean IsStringConst() {
        return this.currentChar == '"';
    }

    private boolean IsCharacterConst() {
        return this.currentChar == '\'';
    }

    private boolean IsIdentifier() {
        return this.IsLetter() || this.IsUnderLine();
    }

    private boolean IsAnnotation() {
        return this.currentChar == '/';
    }

    private boolean IsLetter() {
        return Character.isLetter(this.currentChar);
    }

    private boolean IsUnderLine() {
        return this.currentChar == '_';
    }

    private Token LexerDigit(StringBuilder string) throws IOException {
        while (this.IsDigit()) {
            string.append(this.currentChar);
            this.Read();
        }
        return new Token(TokenType.INTCON, string.toString(), this.lineNumber);
    }

    private Token LexerStringConst(StringBuilder string) throws IOException {
        string.append(this.currentChar);
        this.Read();
        while (this.currentChar != '"') {
            string.append(this.currentChar);
            this.Read();
        }

        string.append(this.currentChar);
        this.Read();
        return new Token(TokenType.STRCON, string.toString(), this.lineNumber);
    }

    private Token LexerCharacterConst(StringBuilder string) throws IOException {
        string.append(this.currentChar);
        this.Read();
        if (this.currentChar == '\\') {
            string.append(this.currentChar);
            this.Read();
        }

        string.append(this.currentChar);
        this.Read();
        if (this.currentChar != '\'') {
            return new Token(TokenType.ERROR, "ERROR", this.lineNumber);
        }

        string.append(this.currentChar);
        this.Read();
        return new Token(TokenType.CHRCON, string.toString(), this.lineNumber);
    }

    private Token LexerIdentifier(StringBuilder string) throws IOException {
        string.append(this.currentChar);
        this.Read();

        while (this.IsLetter() || this.IsDigit() || this.IsUnderLine()) {
            string.append(this.currentChar);
            this.Read();
        }

        String identifier = string.toString();
        return new Token(TokenType.GetTokenType(identifier), identifier, this.lineNumber);
    }

    private Token LexerOp(StringBuilder string) throws IOException {
        return switch (this.currentChar) {
            case '+', '-', '*', '%', ';', ',', '(', ')', '[', ']', '{', '}' ->
                this.LexerSingleOp(string);
            case '<', '>', '!', '=' -> this.LexerTwiceEqual(string);
            case '&', '|' -> this.LexerOpTwiceWithError(string);
            default -> new Token(TokenType.ERROR, string.toString(), this.lineNumber);
        };
    }

    private Token LexerSingleOp(StringBuilder string) throws IOException {
        char character = this.currentChar;
        string.append(character);
        this.Read();
        return new Token(TokenType.GetTokenType(character),
            string.toString(), this.lineNumber);
    }

    private Token LexerTwiceEqual(StringBuilder string) throws IOException {
        char character = this.currentChar;
        string.append(character);
        this.Read();

        if (this.currentChar == '=') {
            string.append(this.currentChar);
            this.Read();
            return new Token(TokenType.GetTokenType(string.toString()),
                string.toString(), this.lineNumber);
        } else {
            return new Token(TokenType.GetTokenType(character),
                string.toString(), this.lineNumber);
        }
    }

    private Token LexerOpTwiceWithError(StringBuilder string) throws IOException {
        char character = this.currentChar;
        string.append(character);
        this.Read();
        if (this.currentChar != character) {
            ErrorRecorder.AddError(new Error(ErrorType.ILLEGAL_SYMBOL, this.lineNumber));
        } else {
            this.Read();
        }
        string.append(character);

        return new Token(TokenType.GetTokenType(character), string.toString(), this.lineNumber);
    }

    private Token LexerAnnotation(StringBuilder string) throws IOException {
        string.append(this.currentChar);
        this.Read();

        // 单行注释
        if (this.currentChar == '/') {
            this.Read();
            while (!this.IsNewLine() && !this.IsEof()) {
                this.Read();
            }
            this.lineNumber++;

            // 利用递归返回下一个token
            this.Read();
            return this.GetToken();
        }
        // 多行注释
        else if (this.currentChar == '*') {
            this.Read();
            while (true) {
                while (this.currentChar != '*') {
                    if (this.IsEof()) {
                        return new Token(TokenType.EOF, "EOF", this.lineNumber);
                    }

                    if (this.IsNewLine()) {
                        this.lineNumber++;
                    }

                    this.Read();
                }

                this.Read();
                if (this.currentChar == '/') {
                    break;
                }
            }

            this.Read();
            return this.GetToken();
        }
        // 一般情况
        else {
            return new Token(TokenType.DIV, string.toString(), this.lineNumber);
        }
    }
}
