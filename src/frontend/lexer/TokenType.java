package frontend.lexer;

public enum TokenType {
    // 标识符
    IDENFR,
    // 数字常量
    INTCON,
    // 字符串常量
    STRCON,
    // 字符常量
    CHRCON,

    // main
    MAINTK,

    // const
    CONSTTK,
    // int
    INTTK,
    // char
    CHARTK,
    // void
    VOIDTK,

    // break
    BREAKTK,
    // continue
    CONTINUETK,

    // if
    IFTK,
    // else
    ELSETK,
    // for
    FORTK,

    // !
    NOT,
    // &&
    AND,
    // ||
    OR,

    // getint()
    GETINTTK,
    // getchar()
    GETCHARTK,
    // printf()
    PRINTFTK,
    // return
    RETURNTK,

    // +
    PLUS,
    // -
    MINU,
    // *
    MULT,
    // /
    DIV,
    // %
    MOD,

    // <
    LSS,
    // <=
    LEQ,
    // >
    GRE,
    // >=
    GEQ,
    // ==
    EQL,
    // !=
    NEQ,
    // =
    ASSIGN,

    // ;
    SEMICN,
    // ,
    COMMA,

    // (
    LPARENT,
    // )
    RPARENT,
    // [
    LBRACK,
    // ]
    RBRACK,
    // {
    LBRACE,
    // }
    RBRACE,

    // EOF
    EOF,
    // error
    ERROR;

    public static TokenType GetTokenType(String identifier) {
        return switch (identifier) {
            case "main" -> TokenType.MAINTK;
            case "const" -> TokenType.CONSTTK;
            case "int" -> TokenType.INTTK;
            case "char" -> TokenType.CHARTK;
            case "void" -> TokenType.VOIDTK;
            case "break" -> TokenType.BREAKTK;
            case "continue" -> TokenType.CONTINUETK;
            case "if" -> TokenType.IFTK;
            case "else" -> TokenType.ELSETK;
            case "for" -> TokenType.FORTK;
            case "getint" -> TokenType.GETINTTK;
            case "getchar" -> TokenType.GETCHARTK;
            case "printf" -> TokenType.PRINTFTK;
            case "return" -> TokenType.RETURNTK;
            default -> TokenType.IDENFR;
        };
    }
}
