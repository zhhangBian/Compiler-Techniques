package optimize;

import midend.llvm.instr.AllocateInstr;
import midend.llvm.instr.Instr;
import midend.llvm.instr.LoadInstr;
import midend.llvm.instr.PhiInstr;
import midend.llvm.instr.StoreInstr;
import midend.llvm.use.IrUse;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrValue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

public class InstrAddPhi {
    private final AllocateInstr allocateInstr;
    private final IrBasicBlock entryBlock;
    private final HashSet<Instr> defineInstrs;
    private final HashSet<Instr> useInstrs;
    private final HashSet<IrBasicBlock> defineBlocks;
    private final HashSet<IrBasicBlock> useBlocks;
    private final Stack<IrValue> valueStack;

    public InstrAddPhi(AllocateInstr allocateInstr, IrBasicBlock entryBlock) {
        this.allocateInstr = allocateInstr;
        this.entryBlock = entryBlock;
        this.defineInstrs = new HashSet<>();
        this.useInstrs = new HashSet<>();
        this.defineBlocks = new HashSet<>();
        this.useBlocks = new HashSet<>();
        this.valueStack = new Stack<>();
    }

    public void AddPhi() {
        // 分析该allocateInstr的define和use关系
        this.BuildDefineUseRelationship();
        // 找出需要添加phi指令的基本块，并添加phi
        this.InsertPhi();
        // 通过DFS进行重命名，同时将相关的alloca, store,load指令删除
        this.ConvertLoadStore(this.entryBlock);
    }

    private void BuildDefineUseRelationship() {
        // 所有使用该allocate的user
        for (IrUse irUse : this.allocateInstr.GetUseList()) {
            Instr userInstr = (Instr) irUse.GetUser();
            // load关系为use关系
            if (userInstr instanceof LoadInstr) {
                this.AddUseInstr(userInstr);
            }
            // store关系为define关系
            else if (userInstr instanceof StoreInstr) {
                this.AddDefineInstr(userInstr);
            }
        }
    }

    private void AddDefineInstr(Instr instr) {
        this.defineInstrs.add(instr);
        this.defineBlocks.add(instr.GetInBasicBlock());
    }

    private void AddUseInstr(Instr instr) {
        this.useInstrs.add(instr);
        this.useBlocks.add(instr.GetInBasicBlock());
    }

    private void InsertPhi() {
        // 需要添加phi的基本块的集合
        HashSet<IrBasicBlock> F = new HashSet<>();
        // 定义变量的基本块的集合
        Stack<IrBasicBlock> W = new Stack<>();

        for (IrBasicBlock defineBlock : this.defineBlocks) {
            W.push(defineBlock);
        }

        while (!W.isEmpty()) {
            IrBasicBlock currentBlock = W.pop();
            // 遍历当前基本块的所有后继基本块
            for (IrBasicBlock Y : currentBlock.GetDominateFrontiers()) {
                // 如果后继基本块不在F中，则将后继基本块加入F中
                if (!F.contains(Y)) {
                    this.InsertPhiInstr(Y);
                    F.add(Y);
                    if (!this.defineBlocks.contains(Y)) {
                        W.push(Y);
                    }
                }
            }
        }
    }

    private void InsertPhiInstr(IrBasicBlock irBasicBlock) {
        PhiInstr phiInstr = new PhiInstr(irBasicBlock);
        irBasicBlock.AddInstr(phiInstr, 0);
        // phi既是define，又是use
        this.AddDefineInstr(phiInstr);
        this.AddUseInstr(phiInstr);
    }

    private void ConvertLoadStore(IrBasicBlock renameBlock) {
        final int stackPointer = this.valueStack.size();

        // 移除与当前allocate相关的全部的load、store指令
        this.RemoveLoadStore(renameBlock);
        // 遍历entry的后续集合，将最新的define（stack.peek）填充进每个后继块的第一个phi指令中
        this.ConvertPhiValue(renameBlock);

        // 对支配快进行dfs
        for (IrBasicBlock dominateBlock : renameBlock.GetDirectDominateBlocks()) {
            this.ConvertLoadStore(dominateBlock);
        }

        // 对dfs过程中压入的元素出栈
        for (int i = 0; i < this.valueStack.size() - stackPointer; i++) {
            this.valueStack.pop();
        }
    }

    private void RemoveLoadStore(IrBasicBlock renameBlock) {
        Iterator<Instr> iterator = renameBlock.GetInstrList().iterator();
        while (iterator.hasNext()) {
            Instr instr = iterator.next();
            // store
            if (instr instanceof StoreInstr storeInstr && this.defineInstrs.contains(instr)) {
                this.valueStack.push(storeInstr.GetValueValue());
                iterator.remove();
            }
            // load
            else if (instr instanceof LoadInstr && this.useInstrs.contains(instr)) {
                // 将所有使用该load指令的指令，改为使用stack.peek()
                instr.ModifyUsersToNewValue(this.valueStack.peek());
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
        }
    }

    private void ConvertPhiValue(IrBasicBlock dfsBlock) {
        // entry的后继块
        for (IrBasicBlock nextBlock : dfsBlock.GetNextBlocks()) {
            Instr firstInstr = nextBlock.GetFirstInstr();
            if (firstInstr instanceof PhiInstr phiInstr && this.useInstrs.contains(firstInstr)) {
                phiInstr.ConvertBlockToValue(this.valueStack.peek(), dfsBlock);
            }
        }
    }
}
