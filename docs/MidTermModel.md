题目要求与语法分析作业基本相同，仅有如下修改：

文法的关键字如（const、int、return、if、printf 等）不区分大小写，但请将所有读入的关键字在词法分析中输出为小写。

【文法】

```
编译单元 CompUnit → {Decl} {FuncDef} MainFuncDef

声明 Decl → ConstDecl | VarDecl

常量声明 ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';' // i

基本类型 BType → 'int' | 'char'

常量定义 ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal // k

常量初值 ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst

变量声明 VarDecl → BType VarDef { ',' VarDef } ';' // i

变量定义 VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal // k

变量初值 InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst

函数定义 FuncDef → FuncType Ident '(' [FuncFParams] ')' Block // j

主函数定义 MainFuncDef → 'int' 'main' '(' ')' Block // j

函数类型 FuncType → 'void' | 'int' | 'char'

函数形参表 FuncFParams → FuncFParam { ',' FuncFParam }

函数形参 FuncFParam → BType Ident ['[' ']'] // k

语句块 Block → '{' { BlockItem } '}'

语句块项 BlockItem → Decl | Stmt

语句 Stmt → LVal '=' Exp ';' // i
| [Exp] ';' // i
| Block
| 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // j
| 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
| 'break' ';' | 'continue' ';' // i
| 'return' [Exp] ';' // i
| LVal '=' 'getint''('')'';' // i j
| LVal '=' 'getchar''('')'';' // i j
| 'printf''('StringConst {','Exp}')'';' // i j

语句 ForStmt → LVal '=' Exp

表达式 Exp → AddExp

条件表达式 Cond → LOrExp

左值表达式 LVal → Ident ['[' Exp ']'] // k

基本表达式 PrimaryExp → '(' Exp ')' | LVal | Number | Character// j

数值 Number → IntConst

字符 Character → CharConst

一元表达式 UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp // j

单目运算符 UnaryOp → '+' | '−' | '!' 注：'!'仅出现在条件表达式中

函数实参表 FuncRParams → Exp { ',' Exp }

乘除模表达式 MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp

加减表达式 AddExp → MulExp | AddExp ('+' | '−') MulExp

关系表达式 RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp

相等性表达式 EqExp → RelExp | EqExp ('==' | '!=') RelExp

逻辑与表达式 LAndExp → EqExp | LAndExp '&&' EqExp // a

逻辑或表达式 LOrExp → LAndExp | LOrExp '||' LAndExp // a

常量表达式 ConstExp → AddExp 注：使用的 Ident 必须是常量
```

【单词类别码】

| 单词名称        | 类别码        | 单词名称    | 类别码       | 单词名称 | 类别码    | 单词名称 | 类别码     |
|-------------|------------|---------|-----------|------|--------|------|---------|
| Ident       | IDENFR     | else    | ELSETK    | void | VOIDTK | ;    | SEMICN  |
| IntConst    | INTCON     | !       | NOT       | *    | MULT   | ,    | COMMA   |
| StringConst | STRCON     | &&      | AND       | /    | DIV    | (    | LPARENT |
| CharConst   | CHRCON     | \|\|    | OR        | %    | MOD    | )    | RPARENT |
| main        | MAINTK     | for     | FORTK     | <    | LSS    | [    | LBRACK  |
| const       | CONSTTK    | getint  | GETINTTK  | <=   | LEQ    | ]    | RBRACK  |
| int         | INTTK      | getchar | GETCHARTK | >    | GRE    | {    | LBRACE  |
| char        | CHARTK     | printf  | PRINTFTK  | >=   | GEQ    | }    | RBRACE  |
| break       | BREAKTK    | return  | RETURNTK  | ==   | EQL    |      |         |
| continue    | CONTINUETK | +       | PLUS      | !=   | NEQ    |      |         |
| if          | IFTK       | -       | MINU      | =    | ASSIGN |      |         |

【样例输入】

```c
coNst INT cONst1 = 1, const2 = -100;

int gets1(int var1,int var2){
return var1 + var2;
}

int main(){
printf("%d",gets1(10, 20));
return 0;
}
```

【样例输出】

```
CONSTTK const
INTTK int
IDENFR cONst1
ASSIGN =
INTCON 1
<Number>
<PrimaryExp>
<UnaryExp>
<MulExp>
<AddExp>
<ConstExp>
<ConstInitVal>
<ConstDef>
COMMA ,
IDENFR const2
ASSIGN =
MINU -
<UnaryOp>
INTCON 100
<Number>
<PrimaryExp>
<UnaryExp>
<UnaryExp>
<MulExp>
<AddExp>
<ConstExp>
<ConstInitVal>
<ConstDef>
SEMICN ;
<ConstDecl>
INTTK int
<FuncType>
IDENFR gets1
LPARENT (
INTTK int
IDENFR var1
<FuncFParam>
COMMA ,
INTTK int
IDENFR var2
<FuncFParam>
<FuncFParams>
RPARENT )
LBRACE {
RETURNTK return
IDENFR var1
<LVal>
<PrimaryExp>
<UnaryExp>
<MulExp>
<AddExp>
PLUS +
IDENFR var2
<LVal>
<PrimaryExp>
<UnaryExp>
<MulExp>
<AddExp>
<Exp>
SEMICN ;
<Stmt>
RBRACE }
<Block>
<FuncDef>
INTTK int
MAINTK main
LPARENT (
RPARENT )
LBRACE {
PRINTFTK printf
LPARENT (
STRCON "%d"
COMMA ,
IDENFR gets1
LPARENT (
INTCON 10
<Number>
<PrimaryExp>
<UnaryExp>
<MulExp>
<AddExp>
<Exp>
COMMA ,
INTCON 20
<Number>
<PrimaryExp>
<UnaryExp>
<MulExp>
<AddExp>
<Exp>
<FuncRParams>
RPARENT )
<UnaryExp>
<MulExp>
<AddExp>
<Exp>
RPARENT )
SEMICN ;
<Stmt>
RETURNTK return
INTCON 0
<Number>
<PrimaryExp>
<UnaryExp>
<MulExp>
<AddExp>
<Exp>
SEMICN ;
<Stmt>
RBRACE }
<Block>
<MainFuncDef>
<CompUnit>
```
