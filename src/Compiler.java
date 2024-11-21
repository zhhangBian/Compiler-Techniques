import backend.BackEnd;
import frontend.FrontEnd;
import midend.MidEnd;
import utils.IOhandler;

import java.io.IOException;

public class Compiler {
    public static void main(String[] args) throws IOException {
        IOhandler.SetIO();

        FrontEnd.SetInput();
        FrontEnd.GenerateTokenList();
        FrontEnd.GenerateAstTree();

        MidEnd.GenerateSymbolTable();
        MidEnd.GenerateIr();

        BackEnd.GenerateMips();

        IOhandler.PrintTokenList();
        IOhandler.PrintAstTree();
        IOhandler.PrintSymbolTable();
        IOhandler.PrintErrorMessage();
        IOhandler.PrintLlvm();
        IOhandler.PrintMips();
    }
}
