package frontend;

import frontend.ast.Node;
import frontend.lexer.Lexer;
import frontend.lexer.Token;
import frontend.lexer.TokenStream;
import frontend.parser.Parser;
import frontend.symbol.SymbolManger;
import frontend.symbol.SymbolTable;
import utils.IOhandler;

import java.io.IOException;
import java.util.ArrayList;

public class FrontEnd {
    private static Lexer lexer;
    private static Parser parser;
    private static SymbolManger symbolManger;

    public static void SetInput() throws IOException {
        lexer = new Lexer(IOhandler.GetInput());
        parser = new Parser();
        symbolManger = new SymbolManger();
    }

    public static void GenerateTokenList() throws IOException {
        lexer.GenerateTokenList();
    }

    public static void GenerateAstTree() {
        parser.SetTokenStream(GetTokenStream());
        parser.GenerateAstTree();
    }

    public static void GenerateSymbolTable() {
        symbolManger.SetRootNode(GetAstTree());
        symbolManger.GenerateSymbolTable();
    }

    public static ArrayList<Token> GetTokenList() {
        return lexer.GetTokenList();
    }

    public static TokenStream GetTokenStream() {
        return new TokenStream(lexer.GetTokenList());
    }

    public static Node GetAstTree() {
        return parser.GetAstTree();
    }

    public static SymbolTable GetRootSymbolTable() {
        return symbolManger.GetRootSymbolTable();
    }
}
