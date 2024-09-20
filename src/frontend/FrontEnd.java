package frontend;

import frontend.lexer.Lexer;

import java.io.Reader;


public class FrontEnd {
    private Reader reader;
    private Lexer lexer;

    public FrontEnd() {

    }

    public void SetInputStream(Reader reader) {
        this.reader = reader;
        this.lexer = new Lexer(reader);
    }
}
