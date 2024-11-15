package midend.visit;

import frontend.ast.Node;
import frontend.ast.exp.Exp;
import frontend.ast.exp.PrimaryExp;
import frontend.ast.exp.UnaryExp;
import frontend.ast.exp.UnaryOp;
import frontend.ast.exp.recursion.AddExp;
import frontend.ast.exp.recursion.MulExp;
import frontend.ast.token.Ident;
import frontend.ast.token.TokenNode;
import midend.llvm.IrBuilder;
import midend.llvm.constant.IrConstantChar;
import midend.llvm.constant.IrConstantInt;
import midend.llvm.instr.AluInstr;
import midend.llvm.instr.CallInstr;
import midend.llvm.instr.CompareInstr;
import midend.llvm.instr.ExtendInstr;
import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrFunction;
import midend.llvm.value.IrValue;
import midend.symbol.FuncSymbol;
import midend.symbol.SymbolManger;

import java.util.ArrayList;

public class VisitorExp {
    public static IrValue VisitExp(Exp exp) {
        AddExp addExp = exp.GetAddExp();
        return VisitAddExp(addExp);
    }

    public static IrValue VisitAddExp(AddExp addExp) {
        ArrayList<Node> nodeList = addExp.GetNodeList();
        MulExp mulExp1 = (MulExp) nodeList.get(0);
        IrValue irValue1 = VisitMulExp(mulExp1);
        IrValue irValue2 = null;

        int index = 1;
        while (index < nodeList.size()) {
            TokenNode op = (TokenNode) nodeList.get(index++);
            MulExp mulExp2 = (MulExp) nodeList.get(index++);
            irValue2 = VisitMulExp(mulExp2);

            AluInstr aluInstr = new AluInstr(IrBuilder.GetFunctionVarName(),
                op.GetSimpleName(), irValue1, irValue2);
            irValue1 = aluInstr;
        }

        return irValue1;
    }

    public static IrValue VisitMulExp(MulExp mulExp) {
        ArrayList<Node> nodeList = mulExp.GetNodeList();
        UnaryExp unaryExp1 = (UnaryExp) nodeList.get(0);
        IrValue irValue1 = VisitUnaryExp(unaryExp1);
        IrValue irValue2 = null;

        int index = 1;
        while (index < nodeList.size()) {
            TokenNode op = (TokenNode) nodeList.get(index++);
            UnaryExp unaryExp2 = (UnaryExp) nodeList.get(index++);
            irValue2 = VisitUnaryExp(unaryExp2);

            AluInstr aluInstr = new AluInstr(IrBuilder.GetFunctionVarName(),
                op.GetSimpleName(), irValue1, irValue2);
            irValue1 = aluInstr;
        }

        return irValue1;
    }

    public static IrValue VisitUnaryExp(UnaryExp unaryExp) {
        if (unaryExp.IsPrimaryType()) {
            return VisitPrimaryExp(unaryExp.GetPrimaryExp());
        } else if (unaryExp.IsIdentType()) {
            return VisitUnaryExpIdent(unaryExp.GetIdent(), unaryExp.GetRealParamList());
        } else if (unaryExp.IsUnaryType()) {
            return VisitUnaryExpUnary(unaryExp.GetUnaryOp(), unaryExp.GetUnaryExp());
        } else {
            throw new RuntimeException("illegal unary exp");
        }
    }

    public static IrValue VisitPrimaryExp(PrimaryExp primaryExp) {
        if (primaryExp.IsExpType()) {
            return VisitExp(primaryExp.GetExp());
        } else if (primaryExp.IsLValType()) {
            return VisitorLVal.VisitLVal(primaryExp.GetLVal(), false);
        } else if (primaryExp.IsNumberType()) {
            return new IrConstantInt(primaryExp.GetNumber().GetValue());
        } else if (primaryExp.IsCharacterType()) {
            return new IrConstantChar(primaryExp.GetCharacter().GetValue());
        } else {
            throw new RuntimeException("illegal primary exp");
        }
    }

    private static IrValue VisitUnaryExpIdent(Ident ident, ArrayList<Exp> realParamList) {
        String identName = ident.GetSimpleName();
        FuncSymbol funcSymbol = (FuncSymbol) SymbolManger.GetSymbol(identName);
        IrFunction irFunction = (IrFunction) funcSymbol.GetIrValue();

        CallInstr callInstr = new CallInstr(IrBuilder.GetFunctionVarName(),
            irFunction, realParamList);
        return callInstr;
    }

    private static IrValue VisitUnaryExpUnary(UnaryOp unaryOp, UnaryExp unaryExp) {
        String op = unaryOp.GetSimpleName();
        IrValue irValue = VisitUnaryExp(unaryExp);
        IrConstantInt constantZero = new IrConstantInt(0);

        switch (op) {
            case "+":
                return irValue;
            case "-":
                return new AluInstr(IrBuilder.GetFunctionVarName(), op, constantZero, irValue);
            case "!":
                CompareInstr compareInstr = new CompareInstr(IrBuilder.GetFunctionVarName(),
                    "==", constantZero, irValue);
                ExtendInstr extendInstr = new ExtendInstr(IrBuilder.GetFunctionVarName(),
                    compareInstr, IrBaseType.INT32);
                return extendInstr;
            default:
                throw new RuntimeException("illegal unary op");
        }
    }
}
