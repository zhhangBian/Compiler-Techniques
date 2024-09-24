package utils;

import frontend.FrontEnd;
import frontend.lexer.Token;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Printer {
    private static FileOutputStream outFile = null;
    private static FileOutputStream errFile = null;

    public static void InitPrinter() throws FileNotFoundException {
        Printer.outFile = new FileOutputStream("output.txt");
        Printer.errFile = new FileOutputStream("error.txt");
    }

    public static void PrintTokenList() throws IOException {
        ArrayList<Token> tokenList = FrontEnd.GetTokenList();
        for (Token token : tokenList) {
            String string = token.GetTokenType() + " " + token.GetStringValue() + "\n";
            outFile.write(string.getBytes());
        }
    }
}
