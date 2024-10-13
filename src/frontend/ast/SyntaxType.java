package frontend.ast;

public enum SyntaxType {
    // 编译单元，一切的开始
    COMP_UNIT("CompUnit"),

    BTYPE("BType"),

    DECL("Decl"),
    CONST_DECL("ConstDecl"),
    VAR_DECL("VarDecl"),

    CONST_DEF("ConstDef"),
    CONST_INIT_VAL("ConstInitVal"),
    VAR_DEF("VarDef"),
    INIT_VAL("InitVal"),

    FUNC_DEF("FuncDef"),
    MAIN_FUNC_DEF("MainFuncDef"),
    FUNC_TYPE("FuncType"),

    FUNC_FORMAL_PARAM("FuncFParam"),
    FUNC_FORMAL_PARAM_S("FuncFParams"),
    FUNC_REAL_PARAM_S("FuncRParams"),

    BLOCK("Block"),
    BLOCK_ITEM("BlockItem"),

    STMT("Stmt"),
    FOR_STMT("ForStmt"),

    UNARY_OP("UnaryOp"),
    IDENT("Ident"),

    EXP("Exp"),
    LVAL_EXP("LVal"),
    PRIMARY_EXP("PrimaryExp"),
    UNARY_EXP("UnaryExp"),
    MUL_EXP("MulExp"),
    ADD_EXP("AddExp"),
    REL_EXP("RelExp"),
    EQ_EXP("EqExp"),
    LAND_EXP("LAndExp"),
    LOR_EXP("LOrExp"),
    CONST_EXP("ConstExp"),
    COND_EXP("Cond"),

    NUMBER("Number"),
    CHARACTER("Character"),

    INT_CONST("IntConst"),
    STRING_CONST("StringConst"),
    CHAR_CONST("CharConst"),

    TOKEN("Token");

    private final String typeName;

    SyntaxType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return this.typeName;
    }
}
