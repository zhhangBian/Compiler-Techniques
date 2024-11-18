package midend.visit;

import frontend.ast.block.Block;
import frontend.ast.func.FuncDef;
import frontend.ast.func.MainFuncDef;
import midend.llvm.IrBuilder;
import midend.llvm.instr.AllocateInstr;
import midend.llvm.instr.StoreInstr;
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
        final IrFunction irFunction = IrBuilder.GetNewFunctionIr(funcSymbol.GetSymbolName(),
            GetIrType(funcSymbol.GetSymbolType()));
        funcSymbol.SetIrValue(irFunction);

        SymbolManger.GoToSonSymbolTable();

        // 创建参数的IR
        ArrayList<Symbol> paramSymbolList = funcSymbol.GetFormalParamList();
        ArrayList<IrParameter> irParameterList = new ArrayList<>();
        for (Symbol symbol : paramSymbolList) {
            IrParameter irParameter = new IrParameter(
                GetIrType(symbol.GetSymbolType()), IrBuilder.GetLocalVarName());
            irFunction.AddParameter(irParameter);
            irParameterList.add(irParameter);
        }

        for (int i = 0; i < irParameterList.size(); i++) {
            IrParameter irParameter = irParameterList.get(i);
            Symbol symbol = paramSymbolList.get(i);

            AllocateInstr allocateInstr = new AllocateInstr(irParameter.GetIrType());
            StoreInstr storeInstr = new StoreInstr(irParameter, allocateInstr);
            symbol.SetIrValue(allocateInstr);
        }

        // 创建Block的IR
        Block block = funcDef.GetBlock();
        VisitorBlock.VisitBlock(block);

        // 检查最后的return
        irFunction.CheckHaveReturn();

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
