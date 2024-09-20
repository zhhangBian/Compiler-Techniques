package frontend.lexer;

import java.io.Reader;

public class Lexer {
    private final Reader reader;

    public Lexer(Reader reader) {
        this.reader = reader;
    }
}
