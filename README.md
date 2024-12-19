# Compiler-Techniques

GBB：my compiler for course Compiler-Techniques

字符流转，在此谱写逻辑华章

![logo](./docs/logo.png)

# 进度表

- 0925-22：09：完成Lexer
- 1013-12：33：完成Parser
- 1020-17：52：完成语义分析、所有错误处理
- 1119-23：00：完成中间代码生成
- 1122-10：00：完成MIPS代码生成
- 1211-20：30：完成代码优化
- 1219-16：00：完成文档
- 1219-17：00：确认最终提交

# 概述

在本学期的编译实验中，我实现了一个简单的编译器，支持将SysY语义编译为MIPS汇编，并进行一些效果还不错的优化。

编译器的实现参考了理论课中给出的五级架构，即：

1. 词法分析：将源程序解析为token序列，并在此过程中进行代码的错误检查，是否有明显的词法错误

2. 语法分析：在token流基础上构建抽象语法树，并在此过程中识别源程序中的不合理成分

3. 语义分析、中间代码生成：基于语法分析得到的抽象语法树，将语法树转换为中间代码形式，同时在这个过程中识别一部分语义问题、符号问题，实现程序的初始架构

   此外，中间代码采用了LLVM，基于其value-use框架搭建了程序内部的依赖关系

4. 代码优化：实现了一些典型的代码优化，如：mem2reg、寄存器分配，LVN、死代码删除、公共子表达式删除、乘除法优化等，实现了较好的效果

5. 生成目标代码：将LLVM-IR转换为MIPS代码，并进行寄存器分配，得到可在目标体系结构上运行的MIPS汇编代码

# 参考编译器设计

在编译器的书写过程中，已有的成熟编译器、往届同学的编译器都给了我很多灵感，在此一并感谢！

文档参考：

1. LLVM官方文档：https://llvm.org/docs/
2. LLVM框架参考：https://github.com/Hyggge/Petrichor
3. 优化框架参考：https://github.com/Thysrael/Pansy
4. 乘除法优化参考：https://github.com/CajZella/Compiler

# 架构概述

在整体的构建中，我将编译器分为了前中后三段，并设计了错误处理端。在编译器的运行中，在每一端各司其职，仅能通过顶层的端级进行交互，达到了“高内聚，低耦合”的效果。

同时，在编译器的书写中，我还尽量运用了多种面向对象设计思想，融入了多种复杂设计模式，使得编译器的整体书写较为优雅，具有良好的阅读效果。

## 文件结构

编译器的整体代码结构为：

```
.
├─backend	# 后端代码
│  └─mips
│      └─assembly
│          ├─data
│          └─fake
├─error		# 错误处理
├─frontend	# 前端代码
│  ├─ast	# 抽象语法树
│  │  ├─block
│  │  ├─decl
│  │  ├─exp
│  │  │  └─recursion
│  │  ├─func
│  │  ├─stmt
│  │  ├─token
│  │  └─value
│  ├─lexer	# lexer
│  └─parser	# parser
├─midend	# 中端
│  ├─llvm	# LLVM相关
│  │  ├─constant
│  │  ├─instr
│  │  │  ├─io
│  │  │  └─phi
│  │  ├─type
│  │  ├─use
│  │  └─value
│  ├─symbol	# 符号表相关
│  └─visit	# 生成中间代码的visit
├─optimize	# 优化相关
└─utils		# 自定义工具
```

编译器的架构遵循的较为规整的前后端分离架构，具备良好的可扩展性。

## 设计理念

整理以`Compiler`类为入口，并封装了`Frontend`、`Midend`、`Backend`三个模块，不同端的交互只能通过与另一端的静态方法进行交互，达成了良好的封装效果。同时，还封装了`ErrorRecorder`类，用于记录编译过程中出现的错误，并进行错误信息的输出。

### 三端分离的设计架构

在我的设计中，前中后三端设计为了静态类，提供了进行交互的静态方法，不暴露内部的接口。

| 方法                | 所属端 | 作用               |
| ------------------- | ------ | ------------------ |
| SetInput            | 前端   | 设置相应的文件输入 |
| GenerateTokenList   | 前端   | 生成token序列      |
| GenerateAstTree     | 前端   | 生成抽象语法树     |
| GetTokenList        | 前端   | 获取token序列      |
| GetAstTree          | 前端   | 获取抽象语法树     |
| GenerateSymbolTable | 中端   | 生成符号表         |
| GenerateIr          | 中端   | 生成中间代码IR     |
| GetSymbolTable      | 中端   | 获取符号表         |
| GetIrModule         | 中端   | 获取IR-module      |
| GenerateMips        | 后端   | 生成mips目标代码   |
| GetMipsModule       | 后端   | 获取mips-module    |

### 输入输出封装

为了统一程序的IO操作，设计了`IOhandler`类，进行输出要求的相关信息，包括中间代码IR、mips目标代码等。与原先的数据生成进行解耦合。

```java
public static void SetIO() throws FileNotFoundException {
    IOhandler.input = new PushbackInputStream(new FileInputStream("testfile.txt"), 16);
    IOhandler.lexerOutputFile = new FileOutputStream("lexer.txt");
    IOhandler.parserOutputFile = new FileOutputStream("parser.txt");
    IOhandler.symbolOutputFile = new FileOutputStream("symbol.txt");
    IOhandler.llvmInitOutputFile = new FileOutputStream("llvm_ir_init.txt");
    IOhandler.llvmPhiOutputFile = new FileOutputStream("llvm_ir_phi.txt");
    IOhandler.llvmOutputFile = new FileOutputStream("llvm_ir.txt");
    IOhandler.mipsOutputFile = new FileOutputStream("mips.txt");
    IOhandler.errorFile = new FileOutputStream("error.txt");
}
```

在`IOhandler`类中，设计了文件的读写路径，提供了相应的文件读写方法。除此以外，程序没有独立的输出方式，最多只能向控制台中输出debug信息。

为了支持文件读写封装，我对所有的需要进行输出的实体，都重写了符合语义的`toString()`方法，使得调用更加自然，只需要在`IOhandler`中调用`write`方法即可。

```java
public static void PrintMips() throws IOException {
    MipsModule mipsModule = BackEnd.GetMipsModule();
    mipsOutputFile.write(mipsModule.toString().getBytes());
}
```

### 静态方法封装

对于封装的每个类，为了减少不必要的函数调用开销，同时在编译器执行过程中大部分**宏观操作**都是确定的，我使用静态方法代替了常见的单例模式进行执行。

这样的好处是显而易见的：在程序的执行过程中函数调用更为简洁优雅，只需要指定类名即可调用相关的方法，在逻辑上也更加自然清洗。这样也客观存在着一些坏处：如方法全局化，静态方法调用的传播特性使得复杂类的静态方法数量激增。不过在衡量取舍后，我觉得这是可以接受的。

### 静态属性的使用

静态的特点在于类之间共享，通过此设置，可以便捷地达到在多个子类之间共享某一属性，从而避免在初始化时频繁的传参操作。并且，java中**一切皆指针**的理念让修改能在多个类之间实现同步。

基于此，我的设计方法是**抽象类中设置共用的静态属性，并配置相应处理方法**，在实际的子类中，通过配置的访问方式访问共享的属性，实现较为简洁的操作。

在进行语法分析时，我设置了读取的token流为Node类中的静态属性，且设置其访问权限为`protected`，从而可以在多个类之间便捷地实现从同一个token序列中进行读取，且还能同步读取进度。

类似的，在进行优化时，优化的对象都是同一个LLVM-module，通过设置了`Optimizer`抽象类的LLVM-module，不同优化方法都是在对同一个module进行前赴后继的优化，实现了较好的效果。

```java
public abstract class Optimizer {
    protected static IrModule irModule;

    public abstract void Optimize();

    public static void SetIrModule(IrModule irModule) {
        Optimizer.irModule = irModule;
    }
}
```

### moudle-builder分离的生产架构

对于每一段，都使用了module+builder的设计思想，将数据生成和数据储存进行分离。

module是实际需要构建的对象，如llvm-module，mips-module等，其并不直接参与其内部储存数据的生成，而是仅负责数据的储存。具体的数据储存全部交给具体的builder进行。

这样进行设计的好处有：

1. 降低了module类的复杂性：无需处理过多的外界输入信息，不需要参与外界数据繁杂的处理，能够对复杂性进行屏蔽，实现良好的封装效果
2. 为扩展性保留空间：尽管实现一个编译器是固定的任务，并不存在着扩展任务，但是依然处于象牙塔中，总要为知识的幻想留下空间：可能的多文件编译，支持的多种后端体系结构等等。将数据的生产和储存进行分离，可以更高地留下扩展空间
3. 更有利于前中后三端架构：在每一端中，可以只储存相应的module，而无需应对数据生成的冗余逻辑和数据结构，更好地有利于高内聚低耦合的设计目标

# 词法分析

词法分析属于前端的解析部分，接受前端的统一调度，通过`GenerateTokenList`开始进行词法分析工作。

## 文件读写

编译器的本质工作是在进行翻译，接受文件输入为唯一的输入来源。从文件中读取字符没有什么特殊的，常见的`setIn`就可以解决此问题。但是为了应对可能的词法分析过程中的读取回溯问题，我使用了`PushbackInputStream`作为输入类，其特点在于**可以将已经读出的字符重新塞回输入流中**，达成回溯效果。

在Lexer中，我也设计了相关的方法来支持从输入流中读取字符，以达成良好的封装效果。

```java
private void Read() throws IOException {
    this.currentChar = (char) reader.read();
}

private void UnRead() throws IOException {
    this.reader.unread(this.currentChar);
}
```

## Token设计

词法分析的核心任务是解析出Token序列，我也对token进行了相应的封装。Token需要记录的信息为：本身的token类型，token本身的字符串值，所属的行号。这些属性一经产生，应当是无法被修改的，也即java中的`final`关键字。

记录的token类型TokenType为一枚举类，这与课程文法文档中的类型一致。

## 词法分析的进行

参照理论课程中的知识，设计了DFA进行词法分析，通过读取相应的First集和Follow集，进行状态转移，实现了对词法分析的进行。

在实际的代码书写中，DFA的进行就是函数的执行，是程序的运行逻辑的转移。对于转移条件，实际上就是有一个个分支进行串联的，组成了实际的判断逻辑。

```java
private Token GetToken() throws IOException {
    StringBuilder string = new StringBuilder();

    // 跳过空白字符
    this.SkipBlank();

    // 处理结束情况
    if (this.IsEof()) {
        return new Token(TokenType.EOF, "EOF", this.lineNumber);
    }
    // 数字常量
    else if (this.IsDigit()) {
        return this.LexerDigit(string);
    }
    // 字符串常量
    else if (this.IsStringConst()) {
        return this.LexerStringConst(string);
    }
    // 字符常量
    else if (this.IsCharacterConst()) {
        return this.LexerCharacterConst(string);
    }
    // 标识符
    else if (this.IsIdentifier()) {
        return this.LexerIdentifier(string);
    }
    // 处理注释
    else if (this.IsAnnotation()) {
        return this.LexerAnnotation(string);
    }
    // 处理一般的符号
    else {
        return this.LexerOp(string);
    }
}
```

## 编码后设计

编码前后的改变主要如下：

1. 在编码前设计中，我对字符`‘\c’`形式和字符串`“aaa”`的设计是不保留单双引号，但是后续发现是需要保留的，这也是出于词法分析知识**忠实地识别token**，并不承载着解析含义的任务

# 语法分析

语法分析是我自认为设计的最优雅的一个部分，使用了封装地极好的递归下降设计，整体的代码设计思路在合理使用面向对象的继承关系后变得极其优雅。实现自顶向下的递归下降，只需要**显示地向每个语法成分中添加子成分**，程序就可以自动开始解析，例如：

```java
@Override
public void Parse() {
    // 只需要识别，不需要判断对错
    // int
    this.AddNode(new TokenNode());
    // main
    this.AddNode(new TokenNode());
    // (
    this.AddNode(new TokenNode());
    // )
    if (GetCurrentTokenType().equals(TokenType.RPARENT)) {
        this.AddNode(new TokenNode());
    } else {
        this.AddMissRParentError();
    }
    // Block
    this.AddNode(new Block());
}
```

## 解析token

语法分析的任务是从token序列中解析出具体的语法成分。但是实际的token解析和语法成分是分开的：随着语法成分的不断推导，当识别到具体的token时，从token流中得到token。

除此以外，未来实现LL(1)文法的Follow集预读，还需要支持体现解析多个token，支持回溯操作。

为此，我设计了单独的`TokenStream`类，负责语法解析过程中的读取token操作，支持设置`readPoint`实现预读和回溯操作。

```java
public Token Peek(int peekStep) {
    if (this.readPoint + peekStep >= this.tokenList.size()) {
        return new Token(TokenType.EOF, "end of token stream", -1);
    }
    return this.tokenList.get(this.readPoint + peekStep);
}

public void SetBackPoint() {
    this.backPoint = this.readPoint;
}

public void GoToBackPoint() {
    this.readPoint = this.backPoint;
}
```

基于此封装，可以较为便捷地实现回溯操作。

## 递归下降的设计

递归下降的核心在于复杂性处理：并不在一个顶层处理所有信息，而是将原始问题解耦为多个子问题后使用递归处理子问题。

我使用了`Parse`函数来进行解析：对于每个语法成分而言，其需要做的是识别出各个语法成分，至于各个语法成分内部的其他成分，交由递归去进行就行了。

这样的递归需要一个边界，所幸语法成分的抽象并不是无止境的，一定会最终落在具体的token上，从token开始逐层向上的抽象构成了设计的文法。

### 语法树的抽象

对于每一个语法成分，均可以视作语法树中的一个结点，其子成分就是语法树中结点的子节点。我将每个语法成分均视作了句法树中的一个结点`Node`，其中包含的元素就是子节点。结点还具有的属性是语法成分的类别`SyntaxType`，与课程的文法文档中的类别设置一致。

```java
protected final SyntaxType syntaxType;
protected final ArrayList<Node> components;
protected boolean printSelf;
```

Node设置为了一个抽象类，仅提供了部分通用方法，具体的实现需要由实际的语法成分进行实现。

```java
public abstract void Parse();

public void Visit() {
    this.components.forEach(Node::Visit);
}

protected void Read() {
    tokenStream.Read();
}

protected Token Peek(int peekStep) {
    return tokenStream.Peek(peekStep);
}

protected void SetBackPoint() {
    tokenStream.SetBackPoint();
}

protected void GoToBackPoint() {
    tokenStream.GoToBackPoint();
}
```

### 语法树的解析

对于语法树的解析过程，我遵循着**先递归解析子节点**，等到子节点解析完成、构建起子树后再将其设置为原始结点的子节点。

对于解析方法，同时也是递归调用方法，我设计了`AddNode`方法，支持了上述操作，可以便捷地实现递归解析操作。

```java
protected void AddNode(Node node) {
    node.Parse();
    this.components.add(node);

```

## 左递归处理

不难发现，涉及左递归的情景只出现了几个计算结点之中，即表达式结点中。这些表达式结点尽管在文法中遵循着不同的层次，但是可以抽象出相同的行为方式，即`node op node`的计算方式，且都遵循着相同的**左递归文法**，可以在面向对象设计中归为同一种基类设计，在基类中实现整体抽象的逻辑实现，在每个类中进行继承，以进行更为定制化的设计。

为此，我首先对文法进行了改写，以便支持实际的编译器解析。随后，为了保证编译器**解析出的语法树依然符合左递归的要求**，我设计了相应的重整函数，以将改写后的文法恢复为原先的左递归形式。

### 文法改写

以`MulExp`为例，其改写前后的文法分别为：

```(空)
改写前：MulExp → UnaryExp | MulExp op UnaryExp
改写后：MulExp → UnaryExp {op UnaryExp}
```

这样改写为规范的BNF范式后，就可以畅快地进行解析了。

### 重整设计

我设计了左递归计算类`RecursionNode`，这是一个抽象类，需要由实际的计算结点进行实现。

在左递归计算类中，我设计了相应的重整函数，核心是从左开始重新建树，每次**选取两个最左结点，将其分别作为左子树和右子树，建立起新的树**。这个方法对于所有的递归计算类都是适用的，因此经过合适的抽象，可以实现复用方法。

```java
@FunctionalInterface
interface Generate1Node1One<T, R> {
    R apply(T t);
}

@FunctionalInterface
interface Generate3Node2One<T, U, V, R> {
    R apply(T t, U u, V v);
}

protected void HandleRecursion(
    Generate1Node1One<Node, Node> constructor1To1,
    Generate3Node2One<Node, Node, Node, Node> constructor3To1) {
    Node exp = this.nodeList.get(0);
    if (this.nodeList.size() > 1) {
        int index = 1;
        exp = constructor1To1.apply(this.nodeList.get(0));
        while (index < this.nodeList.size() - 2) {
            // op
            Node node2 = this.nodeList.get(index++);
            // RecursionNode
            Node node3 = this.nodeList.get(index++);
            // 使用构造函数创建新的节点
            exp = constructor3To1.apply(exp, node2, node3);
        }
        this.components.add(exp);
        this.components.add(this.nodeList.get(index++));
        this.components.add(this.nodeList.get(index));
    } else {
        this.components.add(exp);
    }
}
```

## 编码后设计

编码前后的改变主要如下：

1. token所在的语法成分不需要打印自己的类别，为此在Node类中设置了`printSelf`属性，用于标识是否仅打印自身
2. 原先设计不区分子语法成分具体是那个，以顺序进行区分。为了支持中间代码生成时的选取子语法成分，利用`instance of`进行了硬判断，此设计较为丑陋

# 语义分析

语义分析的核心任务是：建立符号、进行错误处理，我的处理方法均是对语法树进行遍历，在遍历过程中进行相应操作。

## 符号抽象

符号记录了程序中名和含义的抽象，主要涉及到的符号有两类：

1. 值型符号：普通变量、数组、函数参数均属于此类
2. 函数符号：用于标记特定的函数

为了实现两种符号的统一，我使用了`Symbol`作为整体符号的抽象，记录的主要的名和符号的类别`SymbolType`，这是一个枚举类，用于实现快速的分类，也同时对const属性的符号进行了判断。

```java
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
```

在`ValueSymbol`中，还记录了符号的维度、相应数组维数宽度、初值等。其中初值的实现较为丑陋，需要较多的逻辑判断，但是我也没有找到一个更好的方法，也就维持了现在的设计。

在`FuncSymbol`中，记录了参数列表，这样能快速地获取函数符号对应的参数列表。

## 符号表逻辑

对于符号表，其记录的事情有三项：

1. 符号表所在的深度
2. 当前作用域记录的符号信息，我使用哈希表进行实现
3. 指向下一层作用域符号表的指针，这在Java中只需要由一个List实现即可

此外，为了实现遍历的符号表遍历，我还在符号表中记录了每个符号表的**唯一父符号表**，且用一个List实现了**按照访问顺序**排列的符号List，这种冗余设计使得在输出时能够实现符号的有序输出。

```java
private final int depth;

private final ArrayList<Symbol> symbolList;
private final Hashtable<String, Symbol> symbolTable;

private final SymbolTable fatherTable;
private final ArrayList<SymbolTable> sonTables;
```

## 符号表的建立逻辑

在符号表的创建过程中，主要就是遍历语法树，实现对相应符号的记录。我将符号表的创建逻辑和符号表本身解耦，使用了`SymbolManger`来实现对符号表的创建，这样可以便捷地实现创建符号表过程中的作用域切换，同时能够便捷地实现对于符号表父子关系的维护。

```java
public static void GoToFatherSymbolTable() {
    SymbolTable fatherTable = currentSymbolTable.GetFatherTable();
    if (fatherTable != null) {
        currentSymbolTable = fatherTable;
    }
}

public static void CreateSonSymbolTable() {
    SymbolTable sonTable = new SymbolTable(++depth, currentSymbolTable);
    currentSymbolTable.AddSonTable(sonTable);
    currentSymbolTable = sonTable;
}
```

这样的设计具有良好的封装效果，在Manager中，其本身不涉及到具体信息的存储，仅负责创建逻辑的进行，实现了数据和操作的解耦，能够灵活地实现符号表的创建效果。

对于符号的查找，利用唯一的**父符号表指针**，也可以便捷地实现对符号表的不断上溯，实现了对符号作用域的查找：

```java
public static Symbol GetSymbol(String name) {
    SymbolTable table = currentSymbolTable;
    while (table != null) {
        Symbol symbol = table.GetSymbol(name);
        if (symbol != null) {
            return symbol;
        }
        table = table.GetFatherTable();
    }
    return null;
}
```

## 错误处理逻辑

在语义分析过程中，还需要多错误进行处理。需要处理的错误为：

| 错误类别码 | 错误类型                             | 解决思路                                 |
| ---------- | ------------------------------------ | ---------------------------------------- |
| a          | 操作符有错误（`&`和`|`）             | 在语法分析阶段进行                       |
| b          | 名字重定义（函数名和变量名重定义）   | 在当前作用域内查找                       |
| c          | 可执行语句使用未定义的名字           | 利用符号表的父指针，不断向外查找         |
| d          | 函数参数个数不匹配                   | 解析完函数调用后进行确认                 |
| e          | 函数参数类型不匹配                   | 解析完函数调用后进行确认                 |
| h          | 不能改变常量的值                     | 检查Symbol的类型是否为const              |
| f          | 无返回值的函数存在不匹配的return语句 | 在`FuncDef` 内进行处理                   |
| g          | 有返回值的函数缺少return语句         | 在`FuncDef`解析结束后对函数Block进行查找 |

此外，我使用了`ErrorRecorder`来对错误进行记录，通过提供相应的静态方法，可以便捷地实现错误的添加。

```java
public static void AddError(Error error) {
    if (recordError) {
        if (!errorList.containsKey(error.GetLineNumber())) {
            errorList.put(error.GetLineNumber(), error);
        }
    }
}
```

除此以外，为了防止一些bug引起编译解析过程的不能正确进行，我还设置了了一定的debug功能，一些明显的问题，如缺少`;`等，都由程序在记录错误后进行修复，以保证解析的正确进行。

```java
protected void AddMissSemicnError() {
    int line = GetBeforeLineNumber();
    ErrorRecorder.AddError(new Error(ErrorType.MISS_SEMICN, line));

    if (Setting.FIX_ERROR) {
        this.components.add(new TokenNode(new Token(TokenType.SEMICN, ";", line)));
    }
}

protected void AddMissRParentError() {
    int line = GetBeforeLineNumber();
    ErrorRecorder.AddError(new Error(ErrorType.MISS_RPARENT, line));

    if (Setting.FIX_ERROR) {
        this.components.add(new TokenNode(new Token(TokenType.RPARENT, ")", line)));
    }
}

protected void AddMissRBrackError() {
    int line = GetBeforeLineNumber();
    ErrorRecorder.AddError(new Error(ErrorType.MISS_RBRACK, line));

    if (Setting.FIX_ERROR) {
        this.components.add(new TokenNode(new Token(TokenType.RBRACK, "]", line)));
    }
}
```

这样的自动debug可以帮助程序更好运行，以免在出现bug就自动终止，不能解析出更多的信息。

## 编码后设计

编码前后的改变主要如下：

1. 在结束语义分析后，我感到了在语法树中进行相关操作会到来每个结点代码的冗余性，且代码的逻辑较难以控制。为此，在中间代码生成阶段，我使用了**访问者**模式，通过设置公共的访问者`Visitor`，在外层实现了对语法树的访问。不至于随着语法树的关系在内部进行关系的流动，缓解了代码冗余性的问题。
2. 在符号表中用一个List实现了**按照访问顺序**排列的符号List，这种冗余设计使得在输出时能够实现符号的有序输出。这样的冗余是完全面向评测的，实际上是不必要的。

# 中间代码生成

使用了LLVM-IR来作为中间代码。使得程序真正有了可运行的结果。

中间代码生成无疑是工作量最大的一部分，我几乎投入了完整的五天才解决了中间代码生成过程中的全部问题。

## LLVM简介

LLVM IR是一个虚拟指令集，采用三地址形式表示，是强类型语言，并且没有寄存器概念。

### 四个基本层级

LLVM中有四个具有依次包含关系的基本概念：

- `Module`（模块）是一份LLVM IR的**顶层容器**，对应于编译前端的每个翻译单元`TranslationUnit`
  - 每个模块由目标机器信息、全局符号（全局变量和函数）及元信息组成
  - 一个`Module`由若干`GlobalValue`组成，而一个`GlobalValue`可以是**全局变量**`GlobalVariable`，也可以是**函数**`Function`
- `Function`（函数）就是编程语言中的函数，包括函数签名和若干个基本块
  - 函数内的第一个基本块叫做入口基本块
- `BasicBlock`（基本块）是**一组顺序执行的指令集合**，只有一个入口和一个出口，非头尾指令执行时不会违背顺序跳转到其他指令上去
  - **每个基本块最后一条指令一般是跳转指令**（跳转到其它基本块上去）
  - 每个 `BasicBlock` 都有一个 label，label 使得该 `BasicBlock` 有一个符号表的入口点
  - 函数内**最后一个基本块的最后条指令是函数返回指令**，以`terminator instruction`（`ret`、`br` 等）结尾
- `Instruction`（指令）是LLVM IR中的**最小可执行单位**，每一条指令都单占一行

### Value与Use

LLVM 中所有类都直接或间接继承自 `Value`，在 LLVM 中，有**“一切皆 Value”**的说法。通过规整的继承关系，就得到了 LLVM 的类型系统。

为了表达`Value`之间的引用关系，LLVM 中还有一种特殊的`Value`叫做`User`，**其将其他`Value`作为参数**

`Instruction` 继承自 `User`，因此它可以将其他 `Value`作为参数。对于指令`%add1 = add nsw i32 %a, %b`，在 `%add1` 与 `%a`、`%b` 之间**分别构成了`Use`关系**。后续的相关指令也可以继续进行调用，形成Use链

这种指令间的关系正是LLVM的核心之一，实现了SSA形式的中间代码。这样的形式可以方便 LLVM 进行分析和优化，如：

- 可以快速分析两个值是否是同一个值，是否要删除冗余代码
- 如果一个`Value`没有`Use`关系，很可能就是可以删除的冗余代码

在设计中间代码生成的过程中，我一开始并不理解其value-use关系的设计目的，直到后期做到了代码优化，才明白其重要性，可以便捷地找到在函数的值实体之间的依赖关系。

## LLVM中的类型设计

主要分为两类：基本类型和指针类型。

基本类型主要设计为了整型，用`i32, i8, i1`来表示不同的数据位宽：

- `i32`：基本的`int`类型
- `i8`：基本的`char`类型
- `i1`：`bool`类型，用于表示真和假

值得说明的是指针类型，在下面详细描述

### 指针类型的设计

指针类型本身也是一种类型，其特殊点在于指向的类型`targetType`，在数组操作中，会出现二重指针的使用，这对棉麻提出了较高要求。

在一些指令的生成中，一定会生成指针，如创建变量的`alloca`指令返回的类型就是一个指针类型，对于变量的非值引用都是指针。

说来容易，但是在实际开发过程中很多问题都是由指针设计的不完全引起的，需要对指针有较为细致的设计，如下面对LLVM-IR中的地址计算Gep指令中，需要分辨指针的目标类型究竟是普通元素还是数组，是否会产生二重指针的现象。

```java
@Override
public String toString() {
    IrValue pointer = this.GetPointer();
    IrValue offset = this.GetOffset();

    IrPointerType pointerType = (IrPointerType) pointer.GetIrType();
    IrType targetType = pointerType.GetTargetType();

    if (targetType.IsArrayType()) {
        IrArrayType arrayType = (IrArrayType) targetType;
        return this.irName + " = getelementptr inbounds " +
            arrayType + ", " +
            pointerType + " " +
            pointer.GetIrName() + ", i32 0, " +
            offset.GetIrType() + " " +
            offset.GetIrName();
    } else {
        return this.irName + " = getelementptr inbounds " +
            targetType + ", " +
            pointerType + " " +
            pointer.GetIrName() + ", " +
            offset.GetIrType() + " " +
            offset.GetIrName();
    }
}
```

### 类型转换的设计

在值的计算过程中，很可能出现两个参与计算的属性类型是不一致的，需要及时进行转换。

我设计了相应的静态方法来提供相应的转换，能够更加有效地处理计算过程中的类型问题：

```java
public static IrValue ConvertType(IrValue originValue, IrType targetType) {
    IrType originType = originValue.GetIrType();
    if (targetType.IsInt32Type()) {
        if (originType.IsInt32Type()) {
            return originValue;
        } else {
            return new ExtendInstr(originValue, targetType);
        }
    } else if (targetType.IsInt8Type()) {
        if (originType.IsInt32Type()) {
            return new TruncInstr(originValue, targetType);
        } else if (originType.IsInt8Type()) {
            return originValue;
        } else {
            return new ExtendInstr(originValue, targetType);
        }
    } else if (targetType.IsInt1Type()) {
        if (originType.IsInt1Type()) {
            return originValue;
        } else {
            return new TruncInstr(originValue, targetType);
        }
    } else if (targetType.IsArrayType()) {
        IrArrayType arrayType = (IrArrayType) targetType;
        return ConvertType(originValue, arrayType.GetElementType());
    }
    return originValue;
}
```

在计算过程中需要及时进行类型转换，以免出现IR中的计算问题。

## 自动化配置的Builder系统

在生成中间代码的过程中，我设置了使用访问者模式来进行生成，并且配置了相应的自动化配置，使得指令在创建后能自动插入到基本块中，使用面向对象设计中的抽象机制避免了大量冗余的操作，让代码更简洁且更易维护。

### 名的自动分配

在LLVM系统中，命名是关键的，涉及到了value的命名，基本块的命名，函数的命名。直接采用原始程序中的命名显然是不可取的。

为此，在`IrBuilder`中我设计了统一的命名分配方式，能够自动根据相应的类型分配名，使得操作更为简化。

```java
private static final String GLOBAL_VAR_NAME_PREFIX = "@g_";
private static final String STRING_LITERAL_NAME_PREFIX = "@s_";
private static final String LOCAL_VAR_NAME_PREFIX = "%v";
private static final String BasicBlock_NAME_PREFIX = "b_";
private static final String FUNC_NAME_PREFIX = "@f_";

public static String GetFuncName(String name) {
    return name.equals("main") ? "@" + name : FUNC_NAME_PREFIX + name;
}

public static String GetBasicBlockName() {
    return BasicBlock_NAME_PREFIX + basicBlockCount++;
}

public static String GetGlobalVarName() {
    return GLOBAL_VAR_NAME_PREFIX + globalVarNameCount++;
}

public static String GetLocalVarName() {
    int count = localVarNameCountMap.get(currentFunction);
    localVarNameCountMap.put(currentFunction, count + 1);
    return LOCAL_VAR_NAME_PREFIX + count;
}

public static String GetLocalVarName(IrFunction irFunction) {
    int count = localVarNameCountMap.get(irFunction);
    localVarNameCountMap.put(irFunction, count + 1);
    return LOCAL_VAR_NAME_PREFIX + count;
}

public static String GetStringConstName() {
    return STRING_LITERAL_NAME_PREFIX + stringConstNameCount++;
}
```

在这样配置姓名后，避免了人工命名中的冲突问题，也更利于检查后续的bug，解耦了和原先程序的关系。

### 指令的自动插入

对于指令，我设计了抽象类`Instr`来作为所有指令的基类，实现了一些共性方法。

在指令的实例化化过程中，都会调用`Instr`类的`super`构造函数，在父类的构造函数中，会通过`IrBuilder`中的接口向module中插入指令，并配置相应的属性，如指令对应的中间代码名等，是由`IrBuilder`进行统一分配的。

并且，通过构造方法的重载，可以实现更多自定义的操作，如自定义中间代码名，是否需要自动插入基本块中。

```java
public Instr(IrType irType, InstrType instrType) {
    super(irType, IrBuilder.GetLocalVarName());
    this.instrType = instrType;
    // 自动插入
    IrBuilder.AddInstr(this);
}

public Instr(IrType irType, InstrType instrType, String irName) {
    super(irType, irName);
    this.instrType = instrType;
    // 自动插入
    IrBuilder.AddInstr(this);
}

public Instr(IrType irType, InstrType instrType, String irName, boolean autoAdd) {
    super(irType, irName);
    this.instrType = instrType;
    // 自动插入
    if (autoAdd) {
        IrBuilder.AddInstr(this);
    }
}
```

### 基本块的自动插入

基本块是属于函数的，在中间代码的生成过程中，会频繁地设计基本块的创建。为了配置相应的关系，我也设计了相应的自动插入机制，让基本块在创建后能够自动插入到函数IR之中，并且配置当前`IrBuilder`处理的基本块对象。

```java
public static IrBasicBlock GetNewBasicBlockIr() {
    IrBasicBlock basicBlock = new IrBasicBlock(GetBasicBlockName(), currentFunction);
    // 添加到当前的处理中
    currentFunction.AddBasicBlock(basicBlock);

    return basicBlock;
}

public static void SetCurrentBasicBlock(IrBasicBlock irBasicBlock) {
    currentBasicBlock = irBasicBlock;
}
```

## 短路求值的设计

短路求值的设计核心在于理清关系：如果为真，需要跳转到哪里；如果为假，又需要跳转到哪里。在理清关系后，短路求值的实现实际上是相当简单的，只需要不断重复过程，直至具有左递归性质的条件Exp被解析完成。

主要需要进行条件求值的Exp有两类：`LOrExp`和`LAndExp`，可以参考教程中给出的实现，不断重复基本块的创建过程，即可实现对短路求值的实现。

```java
public static void VisitLOrExp(LOrExp lorExp, IrBasicBlock trueBlock, IrBasicBlock falseBlock) {
    ArrayList<LAndExp> landExpList = lorExp.GetLAndExpList();
    for (int i = 0; i < landExpList.size() - 1; i++) {
        IrBasicBlock nextOrBlock = IrBuilder.GetNewBasicBlockIr();
        IrValue andValue = VisitLAndExp(landExpList.get(i), trueBlock, nextOrBlock);
        // 短路求值
        andValue = IrType.ConvertType(andValue, IrBaseType.INT1);
        BranchInstr branchInstr = new BranchInstr(andValue, trueBlock, nextOrBlock);
        // 进入下一个block
        IrBuilder.SetCurrentBasicBlock(nextOrBlock);
    }
    VisitLAndExp(landExpList.get(landExpList.size() - 1), trueBlock, falseBlock);
}

public static IrValue VisitLAndExp(LAndExp landExp, IrBasicBlock trueBlock,
                                   IrBasicBlock falseBlock) {
    ArrayList<EqExp> eqExpList = landExp.GetEqExpList();
    for (int i = 0; i < eqExpList.size() - 1; i++) {
        IrBasicBlock nextEqBlock = IrBuilder.GetNewBasicBlockIr();
        IrValue eqValue = VisitEqExp(eqExpList.get(i));
        // 短路求值
        BranchInstr branchInstr = new BranchInstr(eqValue, nextEqBlock, falseBlock);
        // 进入下一个block
        IrBuilder.SetCurrentBasicBlock(nextEqBlock);
    }
    IrValue eqValue = VisitEqExp(eqExpList.get(eqExpList.size() - 1));
    BranchInstr branchInstr = new BranchInstr(eqValue, trueBlock, falseBlock);

    return eqValue;
}
```

## 编码后设计

编码前后的改变主要如下：

1. 在符号中创建了对应的IR属性`irValue`，以便在解析过程中能够更便捷地获取符号对应的IR属性。
2. 对各种属性的命名添加了前缀，更易区分，也更加规整
3. 在值的解析过程中，需要及时进行类型转换

# 目标代码生成

经过目标代码生成，编译器可以真正产生能运行的汇编代码了，路漫漫那其修远兮！

## 中间代码的翻译

LLVM-IR和mips代码大多是一一对应的，只需要进行忠实的翻译即可，并没有什么特别的。

我在中间代码`Instr`中设置了生成mips指令的方法`toMips`，只需要调用此方法，就可以将指令翻译为mips代码。

并且，在目标代码生成阶段，也配置了相应的自动Builder系统，可以自动插入到mips-module的text段，实现指令的自动化配置。

### 类型转换

由于涉及到了int和char两种类型，需要进行一定的类型转换。

为了进行设计简化，我将int和char都实际按照32位进行存储，但是有一个问题需要进行处理：char类型需要进行截断。

在寄存器的计算过程中，char还是只能获取到8位的数据，故在存储后需要进行合理的截断。

```java
// 缩减即防止溢出，缩减为两位
IrValue originValue = this.GetOriginValue();
Register valueRegister = this.GetRegisterOrK0ForValue(originValue);
this.LoadValueToRegister(originValue, valueRegister);
if (this.targetType.IsInt1Type()) {
    new MipsAlu(MipsAlu.AluType.ANDI, valueRegister, valueRegister, 0x1);
} else if (this.targetType.IsInt8Type()) {
    new MipsAlu(MipsAlu.AluType.ANDI, valueRegister, valueRegister, 0xff);
}
this.SaveRegisterResult(this, valueRegister);
```

## 函数调用设计

在目标代码生成阶段，唯一较为麻烦的就是函数调用，其和中间代码的形式有显著不同，需要进行相应的寄存器保护和传参。主要涉及到了如下几步：

1. 保护现场：将当前使用的寄存器存入栈中。并且，额外地还需要保护`$ra`和`$sp`寄存器，以助于后续的正常使用

   ```java
   private void SaveCurrent(int currentOffset, ArrayList<Register> allocatedRegisterList) {
       // 获取已分配的寄存器列表
       int registerNum = 0;
       for (Register register : allocatedRegisterList) {
           registerNum++;
           new MipsLsu(MipsLsu.LsuType.SW, register, Register.SP,
                       currentOffset - registerNum * 4);
       }
       // 保存SP寄存器和RA寄存器
       new MipsLsu(MipsLsu.LsuType.SW, Register.SP, Register.SP,
                   currentOffset - registerNum * 4 - 4);
       new MipsLsu(MipsLsu.LsuType.SW, Register.RA, Register.SP,
                   currentOffset - registerNum * 4 - 8);
   }
   ```

2. 填入参数：需要将参数填入相应的寄存器`$a1, $a2, $a3`，多余的参数也需要填入对应的栈空间中

   ```java
   private void FillParams(ArrayList<IrValue> paramList, int currentOffset,
                           ArrayList<Register> allocatedRegisterList) {
       for (int i = 0; i < paramList.size(); i++) {
           IrValue param = paramList.get(i);
           // 需要填入相应的寄存器中
           if (i < 3) {
               Register paramRegister = Register.get(Register.A0.ordinal() + i + 1);
               // 如果是参数：由于赋值冲突，从栈中取值，之前保护现场时已存入内存
               if (param instanceof IrParameter) {
                   Register paraRegister = MipsBuilder.GetValueToRegister(param);
                   if (allocatedRegisterList.contains(paraRegister)) {
                       new MipsLsu(MipsLsu.LsuType.LW, paramRegister, Register.SP,
                                   currentOffset - 4 * allocatedRegisterList.indexOf(paraRegister) - 4);
                   } else {
                       this.LoadValueToRegister(param, paramRegister);
                   }
               } else {
                   this.LoadValueToRegister(param, paramRegister);
               }
           }
           // 直接压入栈中
           else {
               Register tempRegister = Register.K0;
               // 如果是参数：由于赋值冲突，从栈中取值
               if (param instanceof IrParameter) {
                   Register paraRegister = MipsBuilder.GetValueToRegister(param);
                   if (allocatedRegisterList.contains(paraRegister)) {
                       new MipsLsu(MipsLsu.LsuType.LW, tempRegister, Register.SP,
                                   currentOffset - 4 * allocatedRegisterList.indexOf(paraRegister) - 4);
                   } else {
                       this.LoadValueToRegister(param, tempRegister);
                   }
               } else {
                   this.LoadValueToRegister(param, tempRegister);
               }
               new MipsLsu(MipsLsu.LsuType.SW, tempRegister, Register.SP,
                           currentOffset - 4 * allocatedRegisterList.size() - 8 - 4 * i - 4);
           }
       }
   }
   ```

3. 设置栈地址：将栈进行偏移，以供在新函数中进行使用

   ```java
   // 设置新的栈地址
   new MipsAlu(MipsAlu.AluType.ADDI, Register.SP, Register.SP, currentOffset);
   ```

4. 跳转函数：设置相应的`jal`指令，进行跳转

   ```java
   // 跳转到函数
   IrFunction targetFunction = this.GetTargetFunction();
   new MipsJump(MipsJump.JumpType.JAL, targetFunction.GetMipsLabel());
   ```

5. 恢复现场：恢复栈、恢复栈中保护的寄存器

   ```java
   private void RecoverCurrent(int formerOffset, ArrayList<Register> allocatedRegisterList) {
       // 恢复RA寄存器和SP寄存器
       new MipsLsu(MipsLsu.LsuType.LW, Register.RA, Register.SP, 0);
       new MipsLsu(MipsLsu.LsuType.LW, Register.SP, Register.SP, 4);
   
       // 恢复原先的寄存器
       // 此时sp已经恢复了
       int registerNum = 0;
       for (Register register : allocatedRegisterList) {
           registerNum++;
           new MipsLsu(MipsLsu.LsuType.LW, register, Register.SP,
                       formerOffset - registerNum * 4);
       }
   }
   ```

6. 处理返回值：处理函数的返回值，将其移动到对应的寄存器中

   ```java
   private void HandleReturnValue() {
       this.SaveRegisterResult(this, Register.V0);
   }
   ```

函数调用是涉及到bug最多的地方，其主要的寄存器分配、栈的设置都极有可能引发bug，需要无比谨慎小心的设计。

## data段初始化

data段对应的是中间代码中的`constant`量，需要进行妥善的初始化。

为了避免mips中的字对齐问题，我选择先进行word类型的初始化，在结束后再进行字符类型的`asciiz`初始化。

同时，对于初值问题，我选择将`word`类用来具有初值的数组元素，对于余下的可能未初始化的部分，使用和`.space`的配合的方式进行初始化。

```java
@Override
public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(this.name + ":");

    int index = this.GetNotZeroIndex();
    if (index >= 0) {
        builder.append("\t.word ");
        StringJoiner joiner = new StringJoiner(", ");
        for (int i = 0; i <= this.GetNotZeroIndex(); i++) {
            joiner.add(this.valueList.get(i).GetIrName());
        }

        builder.append(joiner);
        builder.append("\n\t");
    }
    builder.append("\t.space ");
    builder.append(4 * (this.size - index - 1));

    return builder.toString();
}
```

此外，对于字符类型数组，均设置为了按照int类型进行存储，这在设计上带来了较大的简化，也并未带来功能上的损失。

## 通用方法配置

我还设计了一系列的通用方法，可以实现较大程度的代码复用，主要包括如下：

- 将内存中的值保存到寄存器

  ```java
  protected void LoadValueToRegister(IrValue irValue, Register targetRegister) {
      // 如果是常量
      if (irValue instanceof IrConstant irConstant) {
          new MarsLi(targetRegister, Integer.parseInt(irConstant.GetIrName()));
          return;
      }
      // 如果是指针形变量
      if (irValue instanceof IrGlobalValue irGlobalValue) {
          new MarsLa(targetRegister, irGlobalValue.GetMipsLabel());
          return;
      }
      // 如果有已经分配的寄存器
      Register valueRegister = MipsBuilder.GetValueToRegister(irValue);
      if (valueRegister != null) {
          new MarsMove(targetRegister, valueRegister);
          return;
      }
  
      Integer stackValueOffset = MipsBuilder.GetStackValueOffset(irValue);
      // 若不在内存中，则分配一块
      if (stackValueOffset == null) {
          stackValueOffset = MipsBuilder.AllocateStackForValue(irValue);
      }
      new MipsLsu(MipsLsu.LsuType.LW, targetRegister, Register.SP, stackValueOffset);
  }
  ```

- 获取一个寄存器，没有则使用`$k0`

  ```java
  protected Register GetRegisterOrK0ForValue(IrValue irValue) {
      Register register = MipsBuilder.GetValueToRegister(irValue);
      return register == null ? Register.K0 : register;
  }
  ```

- 获取一个寄存器，没有则使用`$k1`

  ```java
  protected Register GetRegisterOrK1ForValue(IrValue irValue) {
      Register register = MipsBuilder.GetValueToRegister(irValue);
      return register == null ? Register.K1 : register;
  }
  ```

- 保存寄存器中的计算结果，若没分配寄存器则保留到栈上

  ```java
  protected void SaveRegisterResult(IrValue irValue, Register valueRegister) {
      Register register = MipsBuilder.GetValueToRegister(irValue);
      if (register == null) {
          int offset = MipsBuilder.AllocateStackForValue(irValue);
          new MipsLsu(MipsLsu.LsuType.SW, valueRegister, Register.SP, offset);
      } else {
          new MarsMove(register, valueRegister);
      }
  }
  ```

通过这样的设计，可以进行相关方法使用的封装化、规范化，避免可能的bug。

## 编码后设计

编码前后的改变主要如下：

1. 在每条指令翻译后都添加相应的注释输出中间代码，这样可以便于debug
2. 对栈的操作核查了多次，小心再小心

# 代码优化

优化具体可见优化文档。

使用了Setting中的`FINE_TUNING`变量作为优化开关，所有优化均可一键开启关闭。在开启优化开关后，可以取得较好的优化效果。

优化主要分为两个阶段的优化：中端和后端。中端对中间代码先进行优化，随后由后端生成目标代码，再由侯丹优化代码进行进一步的优化。

中端进行的优化较多，单独拆分在了Optimize文件夹中，其中的优化方法均实现了抽象类`Optimizer`，实现了调度逻辑上的统一。后端进行的优化较少，仅作了窥孔优化，于是便没有进一步拆分，直接继承在了后端的MIPS代码生成中，单独创建了窥孔类进行实现该方法。

最终实现的优化为：

- 流图分析
- mem2reg
- 死代码删除
- LVN
- 乘除法优化
- 寄存器分配
- 窥孔优化

# 感想与感谢

感想具体可见感想文档。

感谢：

1. 静谧的夜晚，写完了大部分编译代码
2. 清冷的月光，抚平了时而的迷茫焦虑
3. 愚笨的我，卒终有所获

在编译器的书写过程中，还有许多的感想与感概，更多的就留到感想文档吧！
