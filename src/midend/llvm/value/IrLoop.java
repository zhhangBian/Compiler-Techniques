package midend.llvm.value;

public class IrLoop {
    private final IrBasicBlock condBlock;
    private final IrBasicBlock bodyBlock;
    private final IrBasicBlock stepBlock;
    private final IrBasicBlock followBlock;

    public IrLoop(IrBasicBlock condBlock, IrBasicBlock bodyBlock,
                  IrBasicBlock stepBlock, IrBasicBlock followBlock) {
        this.condBlock = condBlock;
        this.bodyBlock = bodyBlock;
        this.stepBlock = stepBlock;
        this.followBlock = followBlock;
    }

    public IrBasicBlock GetCondBlock() {
        return this.condBlock;
    }

    public IrBasicBlock GetBodyBlock() {
        return this.bodyBlock;
    }

    public IrBasicBlock GetStepBlock() {
        return this.stepBlock;
    }

    public IrBasicBlock GetFollowBlock() {
        return this.followBlock;
    }
}
