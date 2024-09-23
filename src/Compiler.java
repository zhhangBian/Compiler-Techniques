import frontend.FrontEnd;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Compiler {
    private static final FrontEnd frontend = new FrontEnd();

    public static void main(String[] args) throws FileNotFoundException {
        FileInputStream in = new FileInputStream("testfile.txt");
        System.setIn(in);
        PrintStream out = new PrintStream("lexer.txt");
        System.setOut(out);

        frontend.SetInputStream(new InputStreamReader(in));


        PrintLexerResult();
    }

    private static void PrintLexerResult() {

    }
}
