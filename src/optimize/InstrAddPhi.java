package optimize;

import midend.llvm.instr.AllocateInstr;
import midend.llvm.instr.Instr;
import midend.llvm.instr.LoadInstr;
import midend.llvm.instr.StoreInstr;
import midend.llvm.use.IrUse;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrValue;

import java.util.HashSet;

public class InstrAddPhi {
    private final AllocateInstr allocateInstr;
    private final HashSet<Instr> defineInstrSet;
    private final HashSet<Instr> useInstrSet;
    private final HashSet<IrBasicBlock> defineBlockSet;
    private final HashSet<IrBasicBlock> useBlockSet;

    public InstrAddPhi(AllocateInstr allocateInstr) {
        this.allocateInstr = allocateInstr;
        this.defineInstrSet = new HashSet<>();
        this.useInstrSet = new HashSet<>();
        this.defineBlockSet = new HashSet<>();
        this.useBlockSet = new HashSet<>();
    }

    public void AddPhi() {
        // 分析该allocateInstr的define和use关系
        this.BuildUseRelationship();
        // 添加phi
        this.InsertPhi();
    }

    private void BuildUseRelationship() {
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

    private void InsertPhi() {

    }

    private void AddDefineInstr(Instr instr) {
        this.defineInstrSet.add(instr);
        this.defineBlockSet.add(instr.GetInBasicBlock());
    }

    private void AddUseInstr(Instr instr) {
        this.useInstrSet.add(instr);
        this.useBlockSet.add(instr.GetInBasicBlock());
    }
}
