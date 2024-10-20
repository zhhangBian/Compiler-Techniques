package error;

public enum ErrorType {
    ILLEGAL_SYMBOL("a"),
    NAME_REDEFINE("b"),
    NAME_UNDEFINED("c"),
    FUNC_PARAM_NUM_NOT_MATCH("d"),
    FUNC_PARAM_TYPE_NOT_MATCH("e"),
    RETURN_NOT_MATCH("f"),
    MISS_RETURN("g"),
    CHANGE_CONST_VALUE("h"),
    MISS_SEMICN("i"),
    MISS_RPARENT("j"),
    MISS_RBRACK("k"),
    PRINTF_NOT_MATCH("l"),
    BREAK_OR_CONTINUE_IN_NOT_LOOP("m"),
    UNDEFINED("undefined");

    private final String errorValue;

    ErrorType(String errorValue) {
        this.errorValue = errorValue;
    }

    @Override
    public String toString() {
        return this.errorValue;
    }
}
