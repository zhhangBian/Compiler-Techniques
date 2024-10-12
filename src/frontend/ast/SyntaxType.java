package frontend.ast;

public enum SyntaxType {
    // 编译单元，一切的开始
    COMP_UNIT("CompUnit"),

    DECL("Decl"),
    CONST_DECL("ConstDecl"),
    VAR_DECL("VarDecl"),


    BTYPE("BType"),


    CONST_DEF("ConstDef"),
    // 常量初值
    CONST_INIT_VAL("ConstInitVal"),
    // 变量声明

    // 变量定义
    VAR_DEF("VarDef"),
    // 变量初值
    INIT_VAL("InitVal"),
    // 函数定义
    FUNC_DEF("FuncDef"),
    // 主函数定义
    MAIN_FUNC_DEF("MainFuncDef"),
    // 函数类型
    FUNC_TYPE("FuncType"),
    // 函数形参表
    FUNC_FORMAL_PARAM("FuncFParams"),
    // 函数形参
    FUNC_FORMAL_PARAM_S("FuncFParam"),
    FUNC_REAL_PARAM_S("FuncRParams"),

    BLOCK("Block"),
    BLOCK_ITEM("BlockItem"),

    STMT("Stmt"),
    FOR_STMT("ForStmt"),


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
    EXP("Exp"),
    COND_EXP("Cond"),

    // reserved
    NUMBER("Number"),
    CHARACTER("Character"),

    UNARY_OP("UnaryOp"),

    // Ident
    IDENT("Ident"),
    // IntConst
    INT_CONST("IntConst"),
    // StringConst
    STRING_CONST("StringConst"),
    // CharConst
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
