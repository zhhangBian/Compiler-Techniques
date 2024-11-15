package midend.visit;

import frontend.ast.exp.Exp;
import frontend.ast.exp.LVal;
import frontend.ast.token.Ident;
import midend.llvm.IrBuilder;
import midend.llvm.instr.GepInstr;
import midend.llvm.value.IrValue;
import midend.symbol.SymbolManger;
import midend.symbol.ValueSymbol;

import java.util.ArrayList;

public class VisitorLVal {
    public static IrValue VisitLVal(LVal lval) {
        Ident ident = lval.GetIdent();
        ArrayList<Exp> expList = lval.GetExpList();

        ValueSymbol symbol = (ValueSymbol) SymbolManger.GetSymbol(ident.GetSimpleName());
        int dimension = symbol.GetDimension();

        if (dimension == 0) {
            return symbol.GetIrValue();
        } else {
            return new GepInstr(IrBuilder.GetFunctionVarName(),
                symbol.GetIrValue(), VisitorExp.VisitExp(expList.get(0)));
        }
    }
}
