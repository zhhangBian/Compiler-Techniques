import frontend.lexer.Lexer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

public class Compiler {
    public static void main(String[] args) throws FileNotFoundException {
        FileInputStream in = new FileInputStream("testfile.txt");
        System.setIn(in);
        PrintStream out = new PrintStream("lexer.txt");
        System.setOut(out);

        Scanner scanner = new Scanner(System.in);
        Lexer lexer = new Lexer();
    }
}
