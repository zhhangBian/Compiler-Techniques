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
    JUMP,
    CALL,
    RETURN,

    GEP,
    IO,

    PHI,
    PCOPY,
    MOVE
}
