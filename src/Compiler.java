import frontend.FrontEnd;
import utils.IOhandler;

import java.io.IOException;

public class Compiler {
    public static void main(String[] args) throws IOException {
        IOhandler.SetIO();

        FrontEnd.SetInput();
        FrontEnd.GenerateTokenList();

        IOhandler.PrintTokenList();
        IOhandler.PrintErrorMessage();
    }
}
