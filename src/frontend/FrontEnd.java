package frontend;

import frontend.lexer.Lexer;
import frontend.lexer.Token;

import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.ArrayList;

public class FrontEnd {
    private static Lexer lexer;

    public static void SetInputStream(PushbackInputStream reader) throws IOException {
        FrontEnd.lexer = new Lexer(reader);
    }

    public static void GenerateTokenList() throws IOException {
        lexer.GenerateTokenList();
    }

    public static ArrayList<Token> GetTokenList() {
        return lexer.GetTokenList();
    }
}
