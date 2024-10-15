package utils;

import error.Error;
import error.ErrorRecorder;
import frontend.FrontEnd;
import frontend.ast.Node;
import frontend.lexer.Token;
import frontend.symbol.SymbolTable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.ArrayList;

public class IOhandler {
    private static PushbackInputStream input = null;
    private static FileOutputStream lexerOutputFile = null;
    private static FileOutputStream parserOutputFile = null;
    private static FileOutputStream symbolOutputFile = null;
    private static FileOutputStream errorFile = null;

    public static void SetIO() throws FileNotFoundException {
        IOhandler.input = new PushbackInputStream(new FileInputStream("testfile.txt"), 16);
        IOhandler.lexerOutputFile = new FileOutputStream("lexer.txt");
        IOhandler.parserOutputFile = new FileOutputStream("parser.txt");
        IOhandler.symbolOutputFile = new FileOutputStream("symbol.txt");
        IOhandler.errorFile = new FileOutputStream("error.txt");
    }

    public static PushbackInputStream GetInput() {
        return input;
    }

    public static void PrintTokenList() throws IOException {
        ArrayList<Token> tokenList = FrontEnd.GetTokenList();
        for (Token token : tokenList) {
            String string = token.GetTokenType() + " " + token.GetStringValue() + "\n";
            lexerOutputFile.write(string.getBytes());
        }
    }

    public static void PrintAstTree() throws IOException {
        Node astTree = FrontEnd.GetAstTree();
        parserOutputFile.write(astTree.toString().getBytes());
    }

    public static void PrintSymbolTable() throws IOException {
        SymbolTable rootSymbolTable = FrontEnd.GetRootSymbolTable();
        symbolOutputFile.write(rootSymbolTable.toString().getBytes());
    }

    public static void PrintErrorMessage() throws IOException {
        ArrayList<Error> errorList = ErrorRecorder.GetErrorList();
        for (Error error : errorList) {
            errorFile.write((error + "\n").getBytes());
        }
    }
}
