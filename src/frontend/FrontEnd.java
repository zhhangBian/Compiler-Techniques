package frontend;

import frontend.lexer.Lexer;
import frontend.lexer.Token;
import utils.IOhandler;

import java.io.IOException;
import java.util.ArrayList;

public class FrontEnd {
    private static Lexer lexer;

    public static void SetInput() throws IOException {
        FrontEnd.lexer = new Lexer(IOhandler.GetInput());
    }

    public static void GenerateTokenList() throws IOException {
        lexer.GenerateTokenList();
    }

    public static ArrayList<Token> GetTokenList() {
        return lexer.GetTokenList();
    }
}
