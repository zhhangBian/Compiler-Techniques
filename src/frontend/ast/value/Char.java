package frontend.ast.value;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.CharConst;

public class Char extends Node {
    private int value;

    public Char() {
        super(SyntaxType.CHARACTER);
    }

    @Override
    public void Parse() {
        CharConst charConst = new CharConst();
        charConst.Parse();
        this.components.add(charConst);
        this.value = this.GetAsciiValue(charConst.GetSimpleName());
    }

    public int GetValue() {
        return this.value;
    }

    private int GetAsciiValue(String charString) {
        String string = charString.replace("'", "");
        if (string.length() == 1) {
            return string.charAt(0);
        } else {
            return switch (string) {
                case "\\a" -> 7;
                case "\\b" -> 8;
                case "\\t" -> 9;
                case "\\n" -> 10;
                case "\\v" -> 11;
                case "\\f" -> 12;
                case "\\\"" -> 34;
                case "\\'" -> 39;
                case "\\\\" -> 92;
                case "\\0" -> 0;
                default -> throw new RuntimeException("invalid char");
            };
        }
    }
}
