package midend.llvm.instr;

public enum InstrType {
    ALU,
    EXTEND,

    ALLOCATE,
    LOAD,
    STORE,

    BRANCH,
    CALL,
    RETURN,

    IO
}
