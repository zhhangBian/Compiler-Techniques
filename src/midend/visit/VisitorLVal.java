package midend.visit;

import frontend.ast.exp.Exp;
import frontend.ast.exp.LVal;
import frontend.ast.token.Ident;
import midend.llvm.constant.IrConstantInt;
import midend.llvm.instr.GepInstr;
import midend.llvm.instr.LoadInstr;
import midend.llvm.type.IrPointerType;
import midend.llvm.value.IrValue;
import midend.symbol.SymbolManger;
import midend.symbol.ValueSymbol;

import java.util.ArrayList;

public class VisitorLVal {
    // 以下两个方法在数组的赋值上有区别
    public static IrValue VisitLVal(LVal lval, boolean beAssigned) {
        return beAssigned ? VisitLValForAssign(lval) : VisitLValForValue(lval);
    }

    // 返回地址待赋值
    public static IrValue VisitLValForAssign(LVal lval) {
        Ident ident = lval.GetIdent();
        ArrayList<Exp> expList = lval.GetExpList();

        ValueSymbol symbol = (ValueSymbol) SymbolManger.GetSymbol(ident.GetSimpleName());
        int dimension = symbol.GetDimension();

        if (dimension == 0) {
            return symbol.GetIrValue();
        } else {
            // 如果是二维数组，那么需要再取一次
            IrValue pointer = symbol.GetIrValue();
            IrPointerType pointerType = (IrPointerType) pointer.GetIrType();
            if (pointerType.GetTargetType() instanceof IrPointerType) {
                pointer = new LoadInstr(pointer);
            }
            return new GepInstr(pointer, VisitorExp.VisitExp(expList.get(0)));
        }
    }

    // 返回值供赋值
    public static IrValue VisitLValForValue(LVal lval) {
        Ident ident = lval.GetIdent();
        ArrayList<Exp> expList = lval.GetExpList();

        ValueSymbol symbol = (ValueSymbol) SymbolManger.GetSymbol(ident.GetSimpleName());
        if (symbol.GetIrValue() == null) {
            symbol = (ValueSymbol) SymbolManger.GetSymbolFromFather(ident.GetSimpleName());
        }

        int dimension = symbol.GetDimension();
        // TODO：常量优化
        // 不是数组
        if (dimension == 0) {
            return new LoadInstr(symbol.GetIrValue());
        }
        // 是数组
        else {
            // 如果是二维数组，那么需要再取一次
            IrValue pointer = symbol.GetIrValue();
            IrPointerType pointerType = (IrPointerType) pointer.GetIrType();
            if (pointerType.GetTargetType() instanceof IrPointerType) {
                pointer = new LoadInstr(pointer);
            }

            // 没带参数，是指针
            if (expList.isEmpty()) {
                GepInstr gepInstr = new GepInstr(pointer, new IrConstantInt(0));
                return gepInstr;
            }
            // 不是指针
            else {
                GepInstr gepInstr = new GepInstr(pointer, VisitorExp.VisitExp(expList.get(0)));
                LoadInstr loadInstr = new LoadInstr(gepInstr);
                return loadInstr;
            }
        }
    }
}
