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

    public ArrayList<Token> GetTokenList() {
        return this.tokenList;
    }

    public void GenerateTokenList() throws IOException {
        Token token = this.GetToken();
        while (!token.GetTokenType().equals(TokenType.EOF)) {
            this.tokenList.add(token);
            token = this.GetToken();
        }
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
        while (this.IsBlank()) {
            if (this.IsNewLine()) {
                this.lineNumber++;
            }
            this.Read();
        }

        // 处理结束情况
        if (this.IsEof()) {
            return new Token(TokenType.EOF, "EOF", this.lineNumber);
        }

        if (this.currentChar == '+') {
            string.append(this.currentChar);
            this.Read();
            return new Token(TokenType.PLUS, string.toString(), this.lineNumber);
        } else if (this.currentChar == '-') {
            string.append(this.currentChar);
            this.Read();
            return new Token(TokenType.MINU, string.toString(), this.lineNumber);
        } else if (this.currentChar == '*') {
            string.append(this.currentChar);
            this.Read();
            return new Token(TokenType.MULT, string.toString(), this.lineNumber);
        } else if (this.currentChar == '%') {
            string.append(this.currentChar);
            this.Read();
            return new Token(TokenType.MOD, string.toString(), this.lineNumber);
        } else if (this.currentChar == ';') {
            string.append(this.currentChar);
            this.Read();
            return new Token(TokenType.SEMICN, string.toString(), this.lineNumber);
        } else if (this.currentChar == ',') {
            string.append(this.currentChar);
            this.Read();
            return new Token(TokenType.COMMA, string.toString(), this.lineNumber);
        } else if (this.currentChar == '(') {
            string.append(this.currentChar);
            this.Read();
            return new Token(TokenType.LPARENT, string.toString(), this.lineNumber);
        } else if (this.currentChar == ')') {
            string.append(this.currentChar);
            this.Read();
            return new Token(TokenType.RPARENT, string.toString(), this.lineNumber);
        } else if (this.currentChar == '[') {
            string.append(this.currentChar);
            this.Read();
            return new Token(TokenType.LBRACK, string.toString(), this.lineNumber);
        } else if (this.currentChar == ']') {
            string.append(this.currentChar);
            this.Read();
            return new Token(TokenType.RBRACK, string.toString(), this.lineNumber);
        } else if (this.currentChar == '{') {
            string.append(this.currentChar);
            this.Read();
            return new Token(TokenType.LBRACE, string.toString(), this.lineNumber);
        } else if (this.currentChar == '}') {
            string.append(this.currentChar);
            this.Read();
            return new Token(TokenType.RBRACE, string.toString(), this.lineNumber);
        } else if (this.currentChar == '&') {
            string.append(this.currentChar);
            this.Read();
            if (this.currentChar != '&') {
                ErrorRecorder.AddError(new Error(ErrorType.ILLEGAL_SYMBOL, this.lineNumber));
            } else {
                string.append(this.currentChar);
                this.Read();
            }

            return new Token(TokenType.AND, string.toString(), this.lineNumber);
        } else if (this.currentChar == '|') {
            string.append(this.currentChar);
            this.Read();
            if (this.currentChar != '|') {
                ErrorRecorder.AddError(new Error(ErrorType.ILLEGAL_SYMBOL, this.lineNumber));
            } else {
                string.append(this.currentChar);
                this.Read();
            }

            return new Token(TokenType.OR, string.toString(), this.lineNumber);
        } else if (this.currentChar == '<') {
            string.append(this.currentChar);
            this.Read();

            if (this.currentChar == '=') {
                string.append(this.currentChar);
                this.Read();
                return new Token(TokenType.LEQ, string.toString(), this.lineNumber);
            }
            return new Token(TokenType.LSS, string.toString(), this.lineNumber);
        } else if (this.currentChar == '>') {
            string.append(this.currentChar);
            this.Read();

            if (this.currentChar == '=') {
                string.append(this.currentChar);
                this.Read();
                return new Token(TokenType.GEQ, string.toString(), this.lineNumber);
            }
            return new Token(TokenType.GRE, string.toString(), this.lineNumber);
        } else if (this.currentChar == '=') {
            string.append(this.currentChar);
            this.Read();

            if (this.currentChar == '=') {
                string.append(this.currentChar);
                this.Read();
                return new Token(TokenType.EQL, string.toString(), this.lineNumber);
            }
            return new Token(TokenType.ASSIGN, string.toString(), this.lineNumber);
        } else if (this.currentChar == '!') {
            string.append(this.currentChar);
            this.Read();

            if (this.currentChar == '=') {
                string.append(this.currentChar);
                this.Read();
                return new Token(TokenType.NEQ, string.toString(), this.lineNumber);
            }
            return new Token(TokenType.NOT, string.toString(), this.lineNumber);
        }
        // 处理注释
        else if (this.currentChar == '/') {
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

        // 数字常量
        else if (this.IsDigit()) {
            while (this.IsDigit()) {
                string.append(this.currentChar);
                this.Read();
            }
            return new Token(TokenType.INTCON, string.toString(), this.lineNumber);
        }
        // 字符串常量
        else if (this.currentChar == '"') {
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
        // 字符常量
        else if (this.currentChar == '\'') {
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
        // 标识符
        else if (this.IsLetter() || this.IsUnderLine()) {
            string.append(this.currentChar);
            this.Read();

            while (this.IsLetter() || this.IsDigit() || this.IsUnderLine()) {
                string.append(this.currentChar);
                this.Read();
            }

            String identifier = string.toString();
            return new Token(TokenType.GetTokenType(identifier), identifier, this.lineNumber);
        }

        return new Token(TokenType.ERROR, "ERROR", this.lineNumber);
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

    private boolean IsLetter() {
        return Character.isLetter(this.currentChar);
    }

    private boolean IsUnderLine() {
        return this.currentChar == '_';
    }
}
