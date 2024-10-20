package midend.symbol;

public enum SymbolType {
    CHAR("Char"),
    INT("Int"),

    CHAR_ARRAY("CharArray"),
    INT_ARRAY("IntArray"),

    CONST_CHAR("ConstChar"),
    CONST_INT("ConstInt"),

    CONST_CHAR_ARRAY("ConstCharArray"),
    CONST_INT_ARRAY("ConstIntArray"),

    VOID_FUNC("VoidFunc"),
    CHAR_FUNC("CharFunc"),
    INT_FUNC("IntFunc"),

    ERROR("Error");

    private final String typeName;

    SymbolType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return this.typeName;
    }

    public static SymbolType GetVarType(String typeString) {
        return switch (typeString) {
            case "int" -> INT;
            case "char" -> CHAR;
            default -> ERROR;
        };
    }

    public static SymbolType GetVarType(String typeString, int dimension) {
        if (dimension == 0) {
            return GetVarType(typeString);
        }

        return switch (typeString) {
            case "int" -> INT_ARRAY;
            case "char" -> CHAR_ARRAY;
            default -> ERROR;
        };
    }

    public static SymbolType GetConstType(String typeString) {
        return switch (typeString) {
            case "int" -> CONST_INT;
            case "char" -> CONST_CHAR;
            default -> ERROR;
        };
    }

    public static SymbolType GetConstType(String typeString, int dimension) {
        if (dimension == 0) {
            return GetConstType(typeString);
        }

        return switch (typeString) {
            case "int" -> CONST_INT_ARRAY;
            case "char" -> CONST_CHAR_ARRAY;
            default -> ERROR;
        };
    }

    public static SymbolType GetFuncType(String typeString) {
        return switch (typeString) {
            case "int" -> INT_FUNC;
            case "char" -> CHAR_FUNC;
            case "void" -> VOID_FUNC;
            default -> ERROR;
        };
    }
}
