请编译器生成的目标代码为PCODE或MIPS汇编的同学完成本题。

【问题描述】

（1）小译同学在编写testfile时希望能写出 int i = getint(); 这类输入语句。

（2）课程组提供的 SysY 文法中没有位运算操作，现在需实现一个“按位与”的运算操作，并用关键字 bitand 来表示该操作，如表达式 b bitand c 表示 变量 b 与 变量 c 的数值做“按位与”运算得到的结果。

本次考核中，要求同学们在代码生成作业的基础上实现上述两项需求。

【题目要求】

（1）增加的语法成分如下所示：

(i) 变量定义 
```
VarDef → Ident { '[' ConstExp ']' }
       | Ident { '[' ConstExp ']' } '=' InitVal
       | Ident '=' 'getint' '(' ')'
```

(ii) 乘除模表达式 MulExp → UnaryExp | MulExp ('*' | '/' | '%' | 'bitand' ) UnaryExp

（2）为了降低难度，使用输入语句进行变量定义时，变量**只可能**为一个普通 int 类型的变量，**不会**出现对数组变量进行赋值，如 int arr[2] = getint(); 这是不合法的

（3）为了降低难度，按位与运算符号 & 被关键字 bitand 代替，运算效果与 C/Java 语言程序相同，但运算优先级与**乘除模**('*','/','%')为同一优先级，与 C/Java 语言程序规定的位运算优先级**不同**，如 a + b bitand c * d; 按照运算优先级翻译成中间代码为

t1 = b bitand c

t2 = t1 * d

t3 = a + t2

（4）为了降低难度，常量表达式(ConstExp)的计算中**不会**出现按位与(bitand)操作，如 const int p = N bitand M; 和 int a[N bitand M]; （其中 M 和 N 为常量），这些都是不合法的

（5）在新增的语法规则中，bitand 为保留关键字，测试样例中**不会**出现标识符 Ident 为 bitand 的情况

（6）int i = getint(); 等价于 int i; 与 i = getint(); 两条语句

（7）a bitand b 的运算效果等价于 C/Java 语言程序中的 a & b

（8）提示：按位与运算可选用 `and` 指令，其格式与 `add`、`sub`、`mul` 等指令相同。



【输入形式】testfile.txt为符合文法要求的测试程序。另外可能存在来自于标准输入的数据。

【输出形式】按照选择的不同目标码分为三类：

1）生成MIPS的编译器

按如上要求将目标代码生成结果输出至mips.txt中。

2）生成LLVM IR的编译器

按如上要求将目标代码生成结果输出至llvm_ir.txt中。

3）生成PCODE的编译器

按如上要求生成PCODE并解释执行，在pcoderesult.txt中记录解释执行结果。
