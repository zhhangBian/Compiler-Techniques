package frontend.ast;

public enum SyntaxType {
    // 编译单元，一切的开始
    COMP_UNIT("CompUnit"),
    // 声明
    DECL("Decl"),
    // 常量声明
    CONST_DECL("ConstDecl"),
    // 基本类型
    BTYPE("BType"),
    // 常量定义
    CONST_DEF("ConstDef"),
    // 常量初值
    CONST_INITVAL("ConstInitVal"),
    // 变量声明
    VAR_DECL("VarDecl"),
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
    FUNC_FORMAL_PARAMS("FuncFParams"),
    // 函数形参
    FUNC_FORMAL_PARAM("FuncFParam"),


    FUNC_REAL_PARAMS("FuncRParams"),
    BLOCK("Block"),

    STMT("Stmt"),
    ASSIGN_STMT("Stmt"),
    EXP_STMT("Stmt"),
    IF_STMT("Stmt"),
    WHILE_STMT("Stmt"),
    BREAK_STMT("Stmt"),
    CONTINUE_STMT("Stmt"),
    RETURN_STMT("Stmt"),
    GETINT_STMT("Stmt"),
    PRINTF_STMT("Stmt"),
    BLOCK_STMT("Stmt"),


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
    UNARY_OP("UnaryOp"),

    TOKEN("token");

    private final String typeName;

    private SyntaxType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return this.typeName;
    }
}
