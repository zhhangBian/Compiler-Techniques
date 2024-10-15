import frontend.FrontEnd;
import utils.IOhandler;

import java.io.IOException;

public class Compiler {
    public static void main(String[] args) throws IOException {
        IOhandler.SetIO();

        FrontEnd.SetInput();
        FrontEnd.GenerateTokenList();
        FrontEnd.GenerateAstTree();
        //FrontEnd.GenerateSymbolTable();

        IOhandler.PrintTokenList();
        IOhandler.PrintAstTree();
        //IOhandler.PrintSymbolTable();
        IOhandler.PrintErrorMessage();
    }
}
