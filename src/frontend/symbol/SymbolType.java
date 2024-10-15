package frontend.symbol;

public enum SymbolType {
    CHAR("Char"),
    INT("Int"),

    CONST_CHAR("ConstChar"),
    CONST_INT("ConstInt"),

    VOID_FUNC("VoidFunc"),
    CHAR_FUNC("CharFunc"),
    INT_FUNC("IntFunc"),

    CONST_CHAR_ARRAY("ConstCharArray"),
    CONST_INT_ARRAY("ConstIntArray"),

    CHAR_ARRAY("CharArray"),
    INT_ARRAY("IntArray");

    private final String typeName;

    SymbolType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return this.typeName;
    }
}
