package utils;

import error.Error;
import error.ErrorRecorder;
import frontend.FrontEnd;
import frontend.lexer.Token;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Printer {
    private static FileOutputStream outputFile = null;
    private static FileOutputStream errorFile = null;

    public static void InitPrinter() throws FileNotFoundException {
        Printer.outputFile = new FileOutputStream("output.txt");
        Printer.errorFile = new FileOutputStream("error.txt");
    }

    public static void PrintTokenList() throws IOException {
        ArrayList<Token> tokenList = FrontEnd.GetTokenList();
        for (Token token : tokenList) {
            String string = token.GetTokenType() + " " + token.GetStringValue() + "\n";
            outputFile.write(string.getBytes());
        }
    }

    public static void PrintErrorMessage() throws IOException {
        ArrayList<Error> errorList = ErrorRecorder.GetErrorList();
        for (Error error : errorList) {
            String string = error.GetLineNumber() + " " + error.GetErrorType() + "\n";
            errorFile.write(string.getBytes());
        }
    }
}
