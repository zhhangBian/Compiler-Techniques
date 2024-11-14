package midend.visit;

import frontend.ast.block.Block;
import frontend.ast.func.FuncDef;
import frontend.ast.func.MainFuncDef;
import midend.llvm.IrBuilder;
import midend.llvm.instr.AllocateInstr;
import midend.llvm.type.IrBaseType;
import midend.llvm.type.IrPointerType;
import midend.llvm.type.IrType;
import midend.llvm.value.IrFunction;
import midend.llvm.value.IrParameter;
import midend.symbol.FuncSymbol;
import midend.symbol.Symbol;
import midend.symbol.SymbolManger;
import midend.symbol.SymbolType;

import java.util.ArrayList;

public class VisitorFuncDef {
    public static void VisitFuncDef(FuncDef funcDef) {
        FuncSymbol funcSymbol = funcDef.GetSymbol();
        IrFunction irFunction = IrBuilder.GetNewFunctionIr(funcSymbol.GetSymbolName(),
            GetIrType(funcSymbol.GetSymbolType()));

        SymbolManger.GoToSonSymbolTable();

        // 创建参数的IR
        ArrayList<Symbol> paramSymbolList = funcSymbol.GetFormalParamList();
        for (Symbol symbol : paramSymbolList) {
            IrParameter irParameter = new IrParameter(GetIrType(symbol.GetSymbolType()),
                IrBuilder.GetParamName(symbol.GetSymbolName()), irFunction);
            symbol.SetIrValue(irParameter);

            AllocateInstr allocateInstr = new AllocateInstr(IrBuilder.GetFunctionVarName(),
                irParameter.GetIrType());
            IrBuilder.AddInstr(allocateInstr);
        }

        // 创建Block的IR
        Block block = funcDef.GetBlock();
        VisitorBlock.VisitBlock(block);

        SymbolManger.GoToFatherSymbolTable();
    }

    public static void VisitMainFuncDef(MainFuncDef mainFuncDef) {
        IrBuilder.GetNewFunctionIr("main", IrBaseType.INT32);

        SymbolManger.GoToSonSymbolTable();

        // 创建Block的IR
        Block block = mainFuncDef.GetBlock();
        VisitorBlock.VisitBlock(block);

        SymbolManger.GoToFatherSymbolTable();
    }

    private static IrType GetIrType(SymbolType symbolType) {
        return switch (symbolType) {
            case VOID_FUNC -> IrBaseType.VOID;
            case INT_FUNC, INT -> IrBaseType.INT32;
            case CHAR_FUNC, CHAR -> IrBaseType.INT8;
            case INT_ARRAY -> new IrPointerType(IrBaseType.INT32);
            case CHAR_ARRAY -> new IrPointerType(IrBaseType.INT8);
            default -> throw new RuntimeException("illegal func return type");
        };
    }
}
