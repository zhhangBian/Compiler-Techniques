package midend.llvm.instr;

public enum InstrType {
    ALU,
    CMP,
    EXTEND,
    TRUNC,

    ALLOCATE,
    LOAD,
    STORE,

    BRANCH,
    CALL,
    RETURN,

    GEP,
    IO
}
