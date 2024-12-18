# 优化概述

对于优化，使用了Setting中的`FINE_TUNING`变量作为优化开关，所有优化均可一键开启关闭。

优化主要分为两个阶段的优化：中端和后端。中端对中间代码先进行优化，随后由后端生成目标代码，再由侯丹优化代码进行进一步的优化。

中端进行的优化较多，单独拆分在了Optimize文件夹中，其中的优化方法均实现了抽象类`Optimizer`，实现了调度逻辑上的统一。后端进行的优化较少，仅作了窥孔优化，于是便没有进一步拆分，直接继承在了后端的MIPS代码生成中，单独创建了窥孔类进行实现该方法。

在开启优化开关后，可以取得较好的优化效果。

# 中端优化

中端优化指的就是机器无关优化，即对中间代码进行简化。

## LLVM-IR

我的中间代码采用了LLVM-IR的方式。LLVM的特点并不在于其语法特点，而是使用了value-use-user关系来块捷地建立了值之间的使用关系。从这些依赖关系出发，可以便捷地寻找编译过程中值的依赖关系，从而进行优化。

对于value，LLVM中遵循着**一切皆value**的观点，变量是value，指令是value，基本块也是value，只要在程序运行过程中能被抽象为对象的“对象”，均可被抽象为value。

在编译过程中，对user建立其`useValueList`，追踪其使用的value；并也对每个value建立使用其值的使用者列表`userList`，可以边界地进行相关优化。

LLVM绝不是固定的语法，而是其优化的思想方式。中间代码的实现可以千变万化，但是对valu-use的关系追踪是不变的。

## 绘制CFG图

优化的第一步是实现对程序关系的正确建模：

1. 依托于程序逻辑控制指令，建立起基本块的前驱后继关系
   1. 对于jump和branch指令，建立基本块之间的before和next关系
2. 在前驱后继关系的基础上，找到基本块之间的支配关系

在没进行一次优化后，尤其是会对控制逻辑造成修改的死代码删除、基本块合并等优化，程序的控制逻辑都可能会被修改，需要重新建立程序的控制逻辑，从而实现正确的控制逻辑追踪。

### 结点删除法

第一个基本块一定支配所有的基本块，也即从入口块开始，可达所有基本块。

在此基础上，支配关系的建立可以使用结点删除法：假如删除基本块A，从入口块DFS不可达的基本块即A支配的基本块。

```java
// 构建支配关系，使用结点删除法：
// 如果删去图中的某一个结点后，有一些结点变得不可到达，那么这个被删去的结点支配这些变得不可到达的结点
for (IrBasicBlock deleteBlock : blockList) {
    HashSet<IrBasicBlock> visited = new HashSet<>();
    // 总起始结点可达所有结点
    this.SearchDfs(blockList.get(0), deleteBlock, visited);
    for (IrBasicBlock visitBlock : blockList) {
        // 被删除的结点支配
        if (!visited.contains(visitBlock)) {
            visitBlock.AddDominator(deleteBlock);
        }
    }
}
```

## mem2reg

实现mem2reg是实现标准SSA的必由之路：在一次确定的执行过程中，数据的值都是会有对应的对象存储的，这是执行逻辑的先后顺序上决定的。因此可以使用内存作为值的存储地址，用于间接实现SSA的效果。

这样的实现固然简单，但是一个巨大的问题就是太慢了，需要尽可能地使用寄存器来进行代替，也即mem2reg。

但是编译过程是无法预测实际的执行过程的，对于控制流带来的**多个前驱块**的现象，一个对象的值可能会在多处被赋值，但是值的起点是唯一的：以变量声明处作为起点，追踪use和def该变量的指令，将其替换为等价的寄存器之间的操作，实现SSA的效果，也即phi指令，用于解耦内存在SSA上的效果。

这样的操作，本质上是利用的程序的指令逻辑在根本上还是线性的特点，利用**一次只能被一个客体赋值**的特性，将对变量的赋值和取值变为等价的操作，从而消除了原先以内存地址为变量真正标志的方法。

### 从声明语句出发

mem2reg起点是声明语句，也即中间代码中的`alloca`语句。对于每个`alloca`语句，我都单独实力法了一个`InsertPhi`对象来进行其phi指令的插入操作。

对于def关系，是对value的值进行修改的关系，可能会修改其后所有使用到该value的基本块。故需要def之后插入phi指令，将修改的值提前存储到对应的value中，以免造成值的冲突

### 插入phi指令

其流程为：

1. 从函数的第一个基本块出发进行DFS，找到所有对该alloca的value进行def-use操作的指令，建立def和use关系

   ```java
   // load关系为use关系
   if (userInstr instanceof LoadInstr) {
       this.AddUseInstr(userInstr);
   }
   // store关系为define关系
   else if (userInstr instanceof StoreInstr) {
       this.AddDefineInstr(userInstr);
   }
   ```

2. 找出所有需要添加phi的基本块，并添加phi指令。需要添加phi指令的基本块即有def关系的基本块，都可能会对后继基本块造成影响。这里遵循教程上的公式就可以了，核心是值在修改后需要及时传播到后继基本块之中。需要注意的是phi指令需要插入到基本块的第一条指令，以免值的修改不能及时跟踪

3. 在插入phi后，原先的访存操作就没有了意义，需要跟着控制流走一遍，把load的值替换为上一条store的值，这样对值进行了正确跟踪。需要注意的是，由于phi的操作需要跨越控制流，会出现**当仅声明还没真正进行赋值时，就需要插入phi指令**的现象。当程序正确执行时，插入什么值都是没有影响的，但是为了“超时空”插入phi的正确编译，通常使用0代替

   ```java
   // store
   if (instr instanceof StoreInstr storeInstr && this.defineInstrs.contains(instr)) {
       this.valueStack.push(storeInstr.GetValueValue());
       iterator.remove();
   }
   // load
   else if (!(instr instanceof PhiInstr) && this.useInstrs.contains(instr)) {
       instr.ModifyAllUsersToNewValue(this.PeekValueStack());
       iterator.remove();
   }
   // phi
   else if (instr instanceof PhiInstr && this.defineInstrs.contains(instr)) {
       this.valueStack.push(instr);
   }
   // 当前分析的allocate：使用mem2reg后不需要allocate
   else if (instr == this.allocateInstr) {
       iterator.remove();
   }
   ```

### 消除phi

中间代码可以使用phi，但是实际的mips代码中没法使用phi，需要将phi转换为实际的代码。

既然phi的目的是提前进行赋值，那么进行赋值即可，也即，把在phi指令中的value提前存储到后续基本块会使用到该value的user中。

通常都是使用copy进行，这是一个并行的过程：因为phi打破了原先的线性时间布局，强行更改为了预测，所以对copy指令的插入是需要并行完成的。

消除phi的流程为：

1. 遍历前驱基本块，将copy指令插入适当的位置

   1. 如果before只有一个后继，直接插入前驱块

      ```java
      private ParallelCopyInstr InsertCopyDirect(IrBasicBlock beforeBlock) {
          ParallelCopyInstr copyInstr = new ParallelCopyInstr(beforeBlock);
          beforeBlock.AddInstrBeforeJump(copyInstr);
          return copyInstr;
      }
      ```

   2. 如果before有多个后继，需要新建一个中间块：这里会修改原先的流图信息，需要在完成后重新建立流图

      ```java
      // 创建新的中间块并插入
      private ParallelCopyInstr InsertCopyToMiddle(IrBasicBlock beforeBlock, IrBasicBlock nextBlock) {
          IrBasicBlock middleBlock = IrBasicBlock.AddMiddleBlock(beforeBlock, nextBlock);
          ParallelCopyInstr copyInstr = new ParallelCopyInstr(middleBlock);
          middleBlock.AddInstrBeforeJump(copyInstr);
          return copyInstr;
      }
      ```

2. 向phi的copy中填充相应值，可能有多个phi

### 解决循环依赖问题

在转换为copy后，需要检查是否具有循环赋值依赖，即可能存在着`a <- b ; c <- a`的赋值线性。我们希望copy是并行进行的，但是程序终究只能是线性执行，需要使用中间值进行替代，并对后续使用到该值的指令进行值替换。

方法是插入一个中间变量，提前进行赋值，并对后续指令进行只替换，以消除这种循环依赖带来的问题。

```java
if (this.HaveCircleConflict(copyInstr, i)) {
    IrValue middleValue = new IrValue(dstValue.GetIrType(),
                                      dstValue.GetIrName() + "_tmp");
    moveList.add(0, new MoveInstr(dstValue, middleValue, irBasicBlock));
    // 替换后续指令的src
    for (MoveInstr moveInstr : moveList) {
        if (moveInstr.GetSrcValue().equals(dstValue)) {
            moveInstr.SetSrcValue(middleValue);
        }
    }
    fixList.add(new MoveInstr(middleValue, dstValue, irBasicBlock));
}
```

在这样的基础上，就实现了mem2reg，实现了真正的SSA。

## 死代码删除

死代码删除可以分为以下几部分：

- 无用跳转
- 不可达函数
- 不可达基本块
- 无用指令
- 无用分支
- 合并基本块

### 无用跳转

无用跳转实际上也属于不可达指令，但是其存在会对控制逻辑的建立造成显著影响，故需要提前进行删除。

无用跳转，指的是在基本块中**已经有跳转的情况**下还有的跳转。由于基本块的定义，其出口是也是唯一的跳转，所以在有跳转情况下，跳转后续的指令都需要从基本块中删除，以防对控制逻辑的建立造成影响。

```java
boolean hasJump = false;
Iterator<Instr> iterator = irBasicBlock.GetInstrList().iterator();
while (iterator.hasNext()) {
    Instr instr = iterator.next();
    if (hasJump) {
        instr.RemoveAllValueUse();
        iterator.remove();
        continue;
    }

    if (instr instanceof JumpInstr || instr instanceof BranchInstr ||
        instr instanceof ReturnInstr) {
        hasJump = true;
    }
}
```

### 不可达函数

不可达函数即没有被调用的函数，只需要追踪函数调用指令，如果无人调用的函数，**即使函数具有副作用**，一样可以进行删除。

```java
// 删除无用函数
Iterator<IrFunction> iterator = irModule.GetFunctions().iterator();
while (iterator.hasNext()) {
    IrFunction irFunction = iterator.next();
    // 无人调用，删除：即使有sideEffect也没关系
    if (!irFunction.IsMainFunction() && this.callerMap.get(irFunction).isEmpty()) {
        iterator.remove();
    }
}
```

这里的副作用指的是：

1. 会进行IO操作
2. 会对内存进行写操作

这里的操作对可能会对后续的操作造成影响，故称其为具有副作用的。

### 不可达基本块

在进行各种无用指令删除后，会出现**没有前驱基本块的基本块**。由于没有前驱基本块，故这样的基本块是无法到达的，是可以进行删除的。

这样的基本块中还会存在着各种指令，需要对这些指令也进行删除。

同时，在删除后，其后继基本块也可能会没有前驱基本块，这样的删除需要一直进行，直到程序的状态不再发生改变。

```java
// 不可达块，删除
if (visitBlock.GetBeforeBlocks().isEmpty() && !visitBlock.IsEntryBlock()) {
    // 改变关系
    for (IrBasicBlock nextBlock : visitBlock.GetNextBlocks()) {
        nextBlock.GetBeforeBlocks().remove(visitBlock);
        // 消除phi
        for (Instr nextInstr : nextBlock.GetInstrList()) {
            if (nextInstr instanceof PhiInstr phiInstr) {
                phiInstr.RemoveBlock(visitBlock);
            }
        }
    }
    // 删除指令
    for (Instr instr : visitBlock.GetInstrList()) {
        instr.RemoveAllValueUse();
    }

    finished = false;
    iterator.remove();
}
```

### 无用指令

程序中书写的指令并不全是有用的，在编译这门课中，CPU与外界交互的唯二交互方式就是IO操作和向内存中写数据。

基于这种观点，可以先识别出程序中的存指令和IO指令，再沿着其use-value链，找出“有用”的指令，在“有用”的蔓延结束后，其他的指令执行都是无意义的，都是可以删除的。

有用的名字过于难听，不妨称其为活跃指令。活跃指令的求值逻辑如下。本质是沿着use链进行的蔓延操作。

```java
private HashSet<Instr> GetActiveInstrSet() {
    HashSet<Instr> activeInstrSet = new HashSet<>();
    Stack<Instr> todoInstrStack = new Stack<>();
    for (IrFunction irFunction : irModule.GetFunctions()) {
        for (IrBasicBlock irBasicBlock : irFunction.GetBasicBlocks()) {
            for (Instr instr : irBasicBlock.GetInstrList()) {
                if (this.IsCriticalInstr(instr)) {
                    todoInstrStack.push(instr);
                }
            }
        }
    }

    while (!todoInstrStack.isEmpty()) {
        Instr todoInstr = todoInstrStack.pop();
        activeInstrSet.add(todoInstr);
        for (IrValue useValue : todoInstr.GetUseValueList()) {
            if (useValue instanceof Instr useInstr) {
                if (!activeInstrSet.contains(useInstr)) {
                    todoInstrStack.push(useInstr);
                }
                activeInstrSet.add(useInstr);
            }
        }
    }

    return activeInstrSet;
}
```

在得到活跃指令集后，就可以删除不在活跃指令集中的指令。

需要注意的是，指令也是user的一种，需要删除使用的其他value的use记录，以免在后续的追踪中出现问题。

```java
// user中
public void RemoveAllValueUse() {
    // 去除使用的value
    for (IrValue useValue : this.useValueList) {
        useValue.DeleteUser(this);
    }
    this.useValueList.clear();
}
// value中
public void DeleteUser(IrUser user) {
    Iterator<IrUse> iterator = this.useList.iterator();
    while (iterator.hasNext()) {
        IrUse use = iterator.next();
        if (use.GetUser() == user) {
            iterator.remove();
            return;
        }
    }
}
```

以上就是删除指令的操作，涉及到了这样的几个方面：

1. 识别活跃指令
2. 对不是活跃的指令进行删除
3. 在删除指令的过程中，指令也是user，需要对其使用的value注销use关系

### 无用分支

无用分支的删除并不是直接就能进行的，是在进行LVN后将分支条件替换为了常值，因此可以直接将无用分支进行删除。

在删除无用分支后，需要同时注意修改程序的流图关系，不然会出现问题：删除分支后，分支到达的基本块不再是next块，需要注销关系。

这里展示一个如果分支为假的删除情况：

```java
// 为真
if (condValue != 0) {
    jumpInstr = new JumpInstr(trueBlock, irBasicBlock);
    // 更改before-next关系
    irBasicBlock.DeleteNextBlock(falseBlock);
    // 消除phi
    for (Instr nextInstr : falseBlock.GetInstrList()) {
        if (nextInstr instanceof PhiInstr phiInstr) {
            phiInstr.RemoveBlock(irBasicBlock);
        }
    }
}
```

### 合并基本块

合并基本块指的是：前后两个基本块彼此是对方唯一的前驱后继，这时候跳转就是无意义的，可以将两个基本块进行合并。

这样的合并需要进行**握手**操作：

1. A基本块只有唯一后继基本块B
2. 后继基本块只有唯一前驱基本块A

```java
private boolean CanMergeBlock(IrBasicBlock visitBlock) {
     ArrayList<IrBasicBlock> beforeBlockList = visitBlock.GetBeforeBlocks();
     if (beforeBlockList.size() == 1) {
         IrBasicBlock beforeBlock = beforeBlockList.get(0);
         // 前后对接上，则可以合并
         return beforeBlock.GetNextBlocks().size() == 1 &&
             beforeBlock.GetNextBlocks().get(0) == visitBlock;
     }
     return false;
 }
```

达成条件后，即可进行合并，需要进行如下的操作：

1. 合并被合并基本块的指令
2. 修改原先的前驱后继关系：假设A合并其后继B
   1. A的后继基本块修改为B的后继基本块
   2. B的后继基本块的前驱由B修改为A
3. 删除被合并的基本块

## LVN

我实现的LVN只支持在基本块内部合并相同子表达式，同时进行了常数折叠。

### 合并相同子表达式

合并相同子表达式有两步操作：识别和合并。核心在于识别：如何找到相同的子表达式。在识别完成后，由于中间代码采用了SSA的形式，故可以将相同的子表达式替换为同一个value，用value作为子表示的代替。

#### 识别

自然的，可以想到对识别采用哈希，建立一个哈希值进行识别，但是什么样的hash才是好哈希？在子表达式的识别中需要遵循如下的要求：

1. 相同的表达式可以进行识别
2. 满足交换律的计算在左右值交换后还可以以识别为同一个值

由于使用整形来作为哈希值总是不可避免地出现冲突问题，我使用了string类型来作为哈希类型，用于进行匹配。这样的好处有：

1. 字符串的自定义程度较高，可以承载更多的信息。例如使用前缀来区分不同指令，直接将值的信息保存在字符串中
2. 可以方便地实现交换律的实现：对于满足交换律的指令，将其左值固定为字典序较高的哪一个，这样即可实现交换律识别。

求解哈希值的示例为：

```java
@Override
public String GetGvnHash() {
    String valueL = this.GetValueL().GetIrName();
    String valueR = this.GetValueR().GetIrName();
    // 按照字典序
    return (this.aluOp.CanChangeOrder() && valueL.compareTo(valueR) >= 0) ?
        valueL + " " + this.aluOp + " " + valueR :
    valueR + " " + this.aluOp + " " + valueL;
}
```

#### 替换

有哪些指令进行替换是有收益的？我总结为如下几类：

1. 常规计算类指令，即ALU类型指令和MDU类型指令
2. 比较类型指令，用于条件求值，也可以进行替换
3. 数组索引操作：对于形如`a[i] = a[i] + 1`的操作，两侧的地址索引实际上是不变的，进行一次求值即可

在得到hash值之后，就可以快乐地进行替换了，只需要注意对value-use关系的正确维护即可：

```java
Instr instr = iterator.next();
if (this.CanGvnInstr(instr)) {
    String hash = instr.GetGvnHash();
    // 如果存在，则替换值
    if (this.gvnHashMap.containsKey(hash)) {
        instr.ModifyAllUsersToNewValue(this.gvnHashMap.get(hash));
        iterator.remove();
    }
    // else，插入map
    else {
        this.gvnHashMap.put(hash, instr);
        addedInstr.add(instr);
    }
}
// 对于move
else if (instr instanceof MoveInstr moveInstr) {
    IrValue dstValue = moveInstr.GetDstValue();
    IrValue srcValue = moveInstr.GetSrcValue();
    dstValue.ModifyAllUsersToNewValue(srcValue);
}
```

对value-use关系的维护为：将所有使用此value的user替换为使用新value

```java
public void ModifyAllUsersToNewValue(IrValue newValue) {
    ArrayList<IrUser> userList = this.useList.stream().map(IrUse::GetUser).
        collect(Collectors.toCollection(ArrayList::new));
    for (IrUser user : userList) {
        user.ModifyValue(this, newValue);
        this.DeleteUser(user);
        // 将this加入到newValue的useList中
        newValue.AddUse(new IrUse(user, newValue));
    }
}
```

### 常数折叠

常数折叠和子表达式的思路类似，对于确定的常数值，将原先使用的value替换为对应的常数值，直接进行常数运算即可。

常数的分布有如下集中情况：

- 对于两个常量的情况，直接识别出运算，进行计算即可
- 对于一个常量的情况，需要进行更为复杂的判断
  - 可以直接进行优化，如+0，\*0，%0等操作，可以直接得到值
  - 对于复杂一些的情况，涉及到了具体的乘除优化，放到后面再说

## 乘除法优化

乘除法优化是大总称，涉及到了具体的三种优化：

- 乘优化
- 除优化
- 模优化

这里的优化都指的是只有一个操作数是常量的情况。如果两个都是常量，应该在LVN中直接得到计算结果。

### 乘优化

乘能进行的优化是直观的，但也是有限的：将2的幂次的乘替换为sll指令，用位运算代替乘。此外这里还需要注意交换律。

```java
if (num == 0) {
    new MarsLi(registerResult, 0);
    return true;
} else if (num == 1) {
    this.LoadValueToRegister(value, registerResult);
    return true;
} else if (num == -1) {
    this.LoadValueToRegister(value, registerResult);
    new MipsAlu(MipsAlu.AluType.SUBU, registerResult, Register.ZERO, registerResult);
    return true;
} else {
    num = this.GetTwoShiftNum(num);
    if (num != -1) {
        this.LoadValueToRegister(value, registerValue);
        new MipsAlu(MipsAlu.AluType.SLL, registerResult, registerValue, num);
        return true;
    }
}
```

### 除优化

除优化是将除转换为乘法进行，可以消除出发。这样的代价是值得的：除太耗时了，即使是缓慢的乘法，也比出发节省了太多时间。

算法较为复杂，就不详细介绍了。值得注意的是这里开始涉及到中间运算，运算不能一步完成，需要手动注意寄存器分配，不能出现冲突后把值覆盖的现象。

```java
// dst <- n / d
// 这里寄存器分配会乱掉，手动进行一些管理
private void DivSingleConstant(IrValue value, long divisionValue,
                               Register valueRegister, Register resultRegister) {
    Multiplier multiplier = this.GetMultiplier(Math.abs(divisionValue), 31);
    BigInteger multiplyValue = multiplier.GetMultiplyValue();
    int post = multiplier.GetPost();

    this.LoadValueToRegister(value, Register.K1);
    if (multiplyValue.compareTo(BigInteger.ONE.shiftLeft(31)) < 0) {
        new MarsLi(resultRegister, multiplyValue.intValue());
        new MipsMdu(MipsMdu.MduType.MULT, resultRegister, Register.K1);
        new MipsMdu(MipsMdu.MduType.MFHI, resultRegister);
    } else {
        multiplyValue = multiplyValue.subtract(BigInteger.ONE.shiftLeft(32));
        multiplier.SetM(multiplyValue);

        new MarsLi(resultRegister, multiplyValue.intValue());
        new MipsMdu(MipsMdu.MduType.MULT, resultRegister, Register.K1);
        new MipsMdu(MipsMdu.MduType.MFHI, resultRegister);
        new MipsAlu(MipsAlu.AluType.ADDU, resultRegister, resultRegister, Register.K1);
    }

    if (post > 0) {
        new MipsAlu(MipsAlu.AluType.SRA, resultRegister, resultRegister, post);
    }

    new MipsCompare(MipsCompare.CompareType.SLT, Register.K1, Register.K1, Register.ZERO);
    new MipsAlu(MipsAlu.AluType.ADDU, resultRegister, resultRegister, Register.K1);

    if (divisionValue < 0) {
        new MipsAlu(MipsAlu.AluType.SUBU, resultRegister, Register.ZERO, resultRegister);
    }
}
```

### 模优化

模优化的核心是利用了小学数学：$m \% n = m - (m / n * n)$

基于此，在实现除法优化后，可以使用除法优化来帮助进行模优化，总归是有收益的。

这里的中间过程寄存器分配过于复杂，我使用了`$gp`和`$fp`寄存器来暂存中间值。

```java
private void RemOptimize(IrValue valueL, IrValue valueR,
                         Register registerL, Register registerR,
                         Register registerResult) {
    // 均为常数
    if (valueL instanceof IrConstant && valueR instanceof IrConstant) {
        int numL = Integer.parseInt(valueL.GetIrName());
        int numR = Integer.parseInt(valueR.GetIrName());
        new MarsLi(registerResult, numL % numR);
    }
    // 右值为常数
    else if (valueR instanceof IrConstant) {
        int num = Integer.parseInt(valueR.GetIrName());
        // 一般情况：先除优化，再减，总归是优化
        this.LoadValueToRegister(valueL, Register.FP);
        // div中会用到K1
        this.DivSingleConstant(valueL, num, registerL, Register.GP);
        // 进行乘
        int shift = this.GetTwoShiftNum(num);
        if (shift != -1) {
            new MipsAlu(MipsAlu.AluType.SLL, Register.GP, Register.GP, shift);
        }
        // 没有乘优化
        else {
            // 需要手动管理寄存器，不然还是会乱
            new MarsLi(Register.K0, num);
            new MipsMdu(MipsMdu.MduType.MULT, Register.GP, Register.K0);
            new MipsMdu(MipsMdu.MduType.MFLO, Register.GP);
        }
        new MipsAlu(MipsAlu.AluType.SUBU, registerResult, Register.FP, Register.GP);
    } else {
        this.LoadValueToRegister(valueL, registerL);
        this.LoadValueToRegister(valueR, registerR);
        new MipsMdu(MipsMdu.MduType.DIV, registerL, registerR);
        new MipsMdu(MipsMdu.MduType.MFHI, registerResult);
    }
}
```

模优化的效果是十分显著的，有一个测试点直接消除了全部的模运算，带来了较大的性能提升。

## 寄存器分配

限于时间，我并没有实现图着色寄存器分配，而是使用了基于线性扫描的寄存器分配：为每个value记录最后使用其的指令，当不再使用时，就可以释放寄存器。

在实际的分配过程中，采用了DFS的方式：程序的执行本质上还是线性的，使用DFS就是在，模拟线性的执行过程。两个并列的基本块可以公用寄存器，因为线性的执行使得实际上无法共用寄存器。

```java
// 记录当前block的使用信息
this.RecordLastUse(entryBlock, lastUseMap);
// 为首块分配寄存器
this.AllocateOneBlock(entryBlock, lastUseMap, defineSet, neverUseAfterSet);
// 遍历支配结点
for (IrBasicBlock childBlock : entryBlock.GetDirectDominateBlocks()) {
    this.AllocateChildBlock(childBlock);
}
// 释放寄存器
this.FreeDefineValueRegister(defineSet);
// 递归过程：恢复原先的映射关系：将 后继不再使用但是是从前驱block传过来 的变量对应的寄存器映射恢复回来
// 也就是 在neverUsedAfter中，但是不是在当前基本块定义的变量
this.ReCoverRegisterValueMap(defineSet, neverUseAfterSet);
```

在线性扫描的过程中，需要在递归过程中**记录递归前的寄存器使用状态**，在递归完成后进行恢复，以实现并列基本块的寄存器共用。

```java
private void AllocateChildBlock(IrBasicBlock visitBlock) {
    // 程序运行的逻辑本质上还是线性的：如果子节点和子子结点均用不到某值，则可以释放
    // 使用buffer记录该映射关系，在为子节点分配完成后恢复，以免影响其兄弟节点的寄存器分配
    HashMap<Register, IrValue> bufferMap = new HashMap<>();
    // 子程序不使用即不in
    Set<Register> registerSet = new HashSet<>(this.registerValueMap.keySet());
    for (Register register : registerSet) {
        IrValue registerValue = this.registerValueMap.get(register);
        if (!visitBlock.GetInValueSet().contains(registerValue)) {
            bufferMap.put(register, registerValue);
            this.registerValueMap.remove(register);
        }
    }
    // 递归调用
    this.Allocate(visitBlock);
    // 恢复映射关系
    for (Register register : bufferMap.keySet()) {
        this.registerValueMap.put(register, bufferMap.get(register));
    }
}
```

# 后端优化

后端优化指生成汇编指令过程中或生成后进行的代码优化。

## 窥孔优化

后端仅实现了简单的窥孔优化：连续对同一个地址的写只需要保存最后一次。即`sw value1 addr, sw value2 addr`，只需要保留最后一次的即可。

在后端的指令中，我对这样的连续写进行了追踪，破除了这种无用的写后写。

```java
for (int i = 0; i < this.textSegement.size(); i++) {
    MipsAssembly nowInstr = this.textSegement.get(i);
    if (nowInstr instanceof MipsLsu nowLsu && nowLsu.IsStoreType()) {
        if (i == 0) {
            continue;
        }
        MipsAssembly beforeInstr = this.textSegement.get(i - 1);
        // 对同一地址连续写
        if (beforeInstr instanceof MipsLsu beforeLsu && beforeLsu.IsStoreType()) {
            if (nowLsu.GetTarget().equals(beforeLsu.GetTarget())) {
                removeSet.add(beforeInstr);
                finished = false;
            }
        }
    }
}
```

# 优化感想

以上就是我优化的全部部分了。在实现优化后，才感觉编译器真正像是个寄存器了，有了基本的寄存器分配，有了常数合并，可以去除无用代码。

学习计算机的过程中，经常会打趣：“相信编译器的力量”。实际的商用编译器已经达到了十分聪明的优化效果，我的优化还是很基本的。

我看着字符在计算机之中流转，在优化中，逻辑被分析，计算被合并，利用程序的力量，从符合人类思维的程序，梳理了逻辑，迈向了一条更适合计算机执行的道路。