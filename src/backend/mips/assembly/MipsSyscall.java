package backend.mips.assembly;

public class MipsSyscall extends MipsAssembly {
    public MipsSyscall() {
        super(MipsType.SYSCALL);
    }

    @Override
    public String toString() {
        return "syscall";
    }
}
