package midend.symbol;

import midend.llvm.value.IrValue;

public class Symbol {
    private final String symbolName;
    private final SymbolType symbolType;
    private IrValue irValue;

    public Symbol(String symbolName, SymbolType symbolType) {
        this.symbolName = symbolName;
        this.symbolType = symbolType;
    }

    public Symbol(String symbolName, SymbolType symbolType, IrValue irValue) {
        this.symbolName = symbolName;
        this.symbolType = symbolType;
        this.irValue = irValue;
    }

    public String GetSymbolName() {
        return this.symbolName;
    }

    public SymbolType GetSymbolType() {
        return this.symbolType;
    }

    // 设置的一定是一个allocate类型的
    public void SetIrValue(IrValue irValue) {
        this.irValue = irValue;
    }

    public IrValue GetIrValue() {
        return this.irValue;
    }

    @Override
    public String toString() {
        return symbolName + " " + symbolType.toString();
    }
}
