import frontend.FrontEnd;
import utils.Printer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

public class Compiler {
    public static void main(String[] args) throws IOException {
        PushbackInputStream input = new PushbackInputStream(new FileInputStream("testfile.txt"), 16);
        Printer.InitPrinter();

        FrontEnd.SetInputStream(input);
        FrontEnd.GenerateTokenList();
        Printer.PrintTokenList();
    }
}
