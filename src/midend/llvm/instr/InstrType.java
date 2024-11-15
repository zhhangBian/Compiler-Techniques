package midend.llvm.instr;

public enum InstrType {
    ALU,
    CMP,
    EXTEND,

    ALLOCATE,
    LOAD,
    STORE,

    BRANCH,
    CALL,
    RETURN,

    GEP,
    IO
}
