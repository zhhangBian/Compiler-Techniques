package frontend;

import frontend.ast.CompUnit;
import frontend.lexer.Lexer;
import frontend.lexer.Token;
import frontend.lexer.TokenStream;
import frontend.parser.Parser;
import utils.IOhandler;

import java.io.IOException;
import java.util.ArrayList;

public class FrontEnd {
    private static Lexer lexer;
    private static Parser parser;

    public static void SetInput() throws IOException {
        lexer = new Lexer(IOhandler.GetInput());
        parser = new Parser();
    }

    public static void GenerateTokenList() throws IOException {
        lexer.GenerateTokenList();
    }

    public static void GenerateAstTree() {
        parser.SetTokenStream(GetTokenStream());
        parser.GenerateAstTree();
    }

    public static ArrayList<Token> GetTokenList() {
        return lexer.GetTokenList();
    }

    private static TokenStream GetTokenStream() {
        return new TokenStream(lexer.GetTokenList());
    }

    public static CompUnit GetAstTree() {
        return parser.GetAstTree();
    }
}
