package frontend;

import frontend.ast.Node;
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
        parser.SetTokenStream(GetTokenStream());
    }

    public static void GenerateAstTree() {
        parser.GenerateAstTree();
    }

    public static TokenStream GetTokenStream() {
        return new TokenStream(lexer.GetTokenList());
    }

    public static ArrayList<Token> GetTokenList() {
        return lexer.GetTokenList();
    }

    public static Node GetAstTree() {
        return parser.GetAstTree();
    }
}
