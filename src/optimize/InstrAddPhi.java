package optimize;

import midend.llvm.constant.IrConstantInt;
import midend.llvm.instr.AllocateInstr;
import midend.llvm.instr.Instr;
import midend.llvm.instr.LoadInstr;
import midend.llvm.instr.PhiInstr;
import midend.llvm.instr.StoreInstr;
import midend.llvm.use.IrUse;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

public class InstrAddPhi {
    private final AllocateInstr allocateInstr;
    private final IrBasicBlock entryBlock;
    private final HashSet<Instr> defineInstrs;
    private final HashSet<Instr> useInstrs;
    private final ArrayList<IrBasicBlock> defineBlocks;
    private final ArrayList<IrBasicBlock> useBlocks;
    private final Stack<IrValue> valueStack;

    public InstrAddPhi(AllocateInstr allocateInstr, IrBasicBlock entryBlock) {
        this.allocateInstr = allocateInstr;
        this.entryBlock = entryBlock;
        this.defineInstrs = new HashSet<>();
        this.useInstrs = new HashSet<>();
        this.defineBlocks = new ArrayList<>();
        this.useBlocks = new ArrayList<>();
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
        if (!this.defineBlocks.contains(instr.GetInBasicBlock())) {
            this.defineBlocks.add(instr.GetInBasicBlock());
        }
    }

    private void AddUseInstr(Instr instr) {
        this.useInstrs.add(instr);
        if (!this.useBlocks.contains(instr.GetInBasicBlock())) {
            this.useBlocks.add(instr.GetInBasicBlock());
        }
    }

    private void InsertPhi() {
        // 需要添加phi的基本块的集合
        HashSet<IrBasicBlock> addedPhiBlocks = new HashSet<>();

        // 定义变量的基本块的集合
        Stack<IrBasicBlock> defineBlockStack = new Stack<>();
        for (IrBasicBlock defineBlock : this.defineBlocks) {
            defineBlockStack.push(defineBlock);
        }

        while (!defineBlockStack.isEmpty()) {
            IrBasicBlock defineBlock = defineBlockStack.pop();
            // 遍历当前基本块的所有后继基本块
            for (IrBasicBlock frontierBlock : defineBlock.GetDominateFrontiers()) {
                // 如果后继基本块不在F中，则将后继基本块加入F中
                if (!addedPhiBlocks.contains(frontierBlock)) {
                    this.InsertPhiInstr(frontierBlock);
                    addedPhiBlocks.add(frontierBlock);
                    // phi也进行定义变量
                    if (!defineBlockStack.contains(frontierBlock)) {
                        defineBlockStack.push(frontierBlock);
                    }
                }
            }
        }
    }

    private void InsertPhiInstr(IrBasicBlock irBasicBlock) {
        PhiInstr phiInstr = new PhiInstr(this.allocateInstr.GetTargetType(), irBasicBlock);
        irBasicBlock.AddInstr(phiInstr, 0);
        // phi既是define，又是use
        this.AddDefineInstr(phiInstr);
        this.AddUseInstr(phiInstr);
    }

    private void ConvertLoadStore(IrBasicBlock renameBlock) {
        // 移除与当前allocate相关的全部的load、store指令
        int count = this.RemoveBlockLoadStore(renameBlock);
        // 遍历entry的后续集合，将最新的define填充进每个后继块的第一个phi指令中
        this.ConvertPhiValue(renameBlock);

        // 对支配块进行dfs
        for (IrBasicBlock dominateBlock : renameBlock.GetDirectDominateBlocks()) {
            this.ConvertLoadStore(dominateBlock);
        }

        // 对dfs过程中压入的元素出栈
        for (int i = 0; i < count; i++) {
            this.valueStack.pop();
        }
    }

    private int RemoveBlockLoadStore(IrBasicBlock visitBlock) {
        int count = 0;
        Iterator<Instr> iterator = visitBlock.GetInstrList().iterator();
        while (iterator.hasNext()) {
            Instr instr = iterator.next();
            // store
            if (instr instanceof StoreInstr storeInstr && this.defineInstrs.contains(instr)) {
                this.valueStack.push(storeInstr.GetValueValue());
                count++;
                iterator.remove();
            }
            // load
            else if (instr instanceof LoadInstr && this.useInstrs.contains(instr)) {
                // 将所有使用该load指令的指令，改为使用stack.peek()
                instr.ModifyUsersToNewValue(this.PeekValueStack());
                iterator.remove();
            }
            // phi
            else if (instr instanceof PhiInstr && this.defineInstrs.contains(instr)) {
                this.valueStack.push(instr);
                count++;
            }
            // 当前分析的allocate：使用mem2reg后不需要allocate
            else if (instr == this.allocateInstr) {
                iterator.remove();
            }
        }
        return count;
    }

    private void ConvertPhiValue(IrBasicBlock visitBlock) {
        for (IrBasicBlock nextBlock : visitBlock.GetNextBlocks()) {
            Instr firstInstr = nextBlock.GetFirstInstr();
            if (firstInstr instanceof PhiInstr phiInstr && this.useInstrs.contains(firstInstr)) {
                phiInstr.ConvertBlockToValue(this.PeekValueStack(), visitBlock);
            }
        }
    }

    private IrValue PeekValueStack() {
        return this.valueStack.isEmpty() ? new IrConstantInt(0) : this.valueStack.peek();
    }
}
