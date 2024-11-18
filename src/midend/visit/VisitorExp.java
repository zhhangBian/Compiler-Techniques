package midend.visit;

import frontend.ast.Node;
import frontend.ast.exp.Cond;
import frontend.ast.exp.Exp;
import frontend.ast.exp.PrimaryExp;
import frontend.ast.exp.UnaryExp;
import frontend.ast.exp.UnaryOp;
import frontend.ast.exp.recursion.AddExp;
import frontend.ast.exp.recursion.EqExp;
import frontend.ast.exp.recursion.LAndExp;
import frontend.ast.exp.recursion.LOrExp;
import frontend.ast.exp.recursion.MulExp;
import frontend.ast.exp.recursion.RelExp;
import frontend.ast.token.Ident;
import frontend.ast.token.TokenNode;
import midend.llvm.IrBuilder;
import midend.llvm.constant.IrConstantChar;
import midend.llvm.constant.IrConstantInt;
import midend.llvm.instr.AluInstr;
import midend.llvm.instr.BranchInstr;
import midend.llvm.instr.CallInstr;
import midend.llvm.instr.CompareInstr;
import midend.llvm.instr.ExtendInstr;
import midend.llvm.type.IrBaseType;
import midend.llvm.type.IrType;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrFunction;
import midend.llvm.value.IrValue;
import midend.symbol.FuncSymbol;
import midend.symbol.SymbolManger;

import java.util.ArrayList;

public class VisitorExp {
    public static IrValue VisitExp(Exp exp) {
        return VisitAddExp(exp.GetAddExp());
    }

    public static IrValue VisitAddExp(AddExp addExp) {
        ArrayList<Node> nodeList = addExp.GetNodeList();
        MulExp mulExp1 = (MulExp) nodeList.get(0);
        IrValue irValue1 = VisitMulExp(mulExp1);
        IrValue irValue2 = null;

        int index = 1;
        while (index < nodeList.size()) {
            final TokenNode op = (TokenNode) nodeList.get(index++);
            MulExp mulExp2 = (MulExp) nodeList.get(index++);
            irValue2 = VisitMulExp(mulExp2);

            // 进行类型转换
            irValue1 = IrType.ConvertType(irValue1, IrBaseType.INT32);
            irValue2 = IrType.ConvertType(irValue2, IrBaseType.INT32);

            AluInstr aluInstr = new AluInstr(op.GetSimpleName(), irValue1, irValue2);
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
            final TokenNode op = (TokenNode) nodeList.get(index++);
            UnaryExp unaryExp2 = (UnaryExp) nodeList.get(index++);
            irValue2 = VisitUnaryExp(unaryExp2);

            // 进行类型转换
            irValue1 = IrType.ConvertType(irValue1, IrBaseType.INT32);
            irValue2 = IrType.ConvertType(irValue2, IrBaseType.INT32);

            AluInstr aluInstr = new AluInstr(op.GetSimpleName(), irValue1, irValue2);
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

        ArrayList<IrValue> paramList = new ArrayList<>();
        for (Exp para : realParamList) {
            paramList.add(VisitExp(para));
        }

        CallInstr callInstr = new CallInstr(irFunction, paramList);
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
                return new AluInstr(op, constantZero, irValue);
            case "!":
                irValue = IrType.ConvertType(irValue, IrBaseType.INT32);
                CompareInstr compareInstr = new CompareInstr("==", constantZero, irValue);
                ExtendInstr extendInstr = new ExtendInstr(compareInstr, IrBaseType.INT32);
                return extendInstr;
            default:
                throw new RuntimeException("illegal unary op");
        }
    }

    // 简化逻辑，抽象为为真进入的block和为假进入的block
    public static void VisitCond(Cond cond, IrBasicBlock trueBlock, IrBasicBlock falseBlock) {
        VisitLOrExp(cond.GetLOrExp(), trueBlock, falseBlock);
    }

    public static void VisitLOrExp(LOrExp lorExp, IrBasicBlock trueBlock, IrBasicBlock falseBlock) {
        ArrayList<LAndExp> landExpList = lorExp.GetLAndExpList();
        for (int i = 0; i < landExpList.size() - 1; i++) {
            IrBasicBlock nextOrBlock = IrBuilder.GetNewBasicBlockIr();
            IrValue andValue = VisitLAndExp(landExpList.get(i), trueBlock, nextOrBlock);
            // 短路求值
            andValue = IrType.ConvertType(andValue, IrBaseType.INT1);
            BranchInstr branchInstr = new BranchInstr(andValue, trueBlock, nextOrBlock);
            // 进入下一个block
            IrBuilder.SetCurrentBasicBlock(nextOrBlock);
        }
        VisitLAndExp(landExpList.get(landExpList.size() - 1), trueBlock, falseBlock);
    }

    public static IrValue VisitLAndExp(LAndExp landExp, IrBasicBlock trueBlock,
                                       IrBasicBlock falseBlock) {
        ArrayList<EqExp> eqExpList = landExp.GetEqExpList();
        for (int i = 0; i < eqExpList.size() - 1; i++) {
            IrBasicBlock nextEqBlock = IrBuilder.GetNewBasicBlockIr();
            IrValue eqValue = VisitEqExp(eqExpList.get(i));
            // 短路求值
            BranchInstr branchInstr = new BranchInstr(eqValue, nextEqBlock, falseBlock);
            // 进入下一个block
            IrBuilder.SetCurrentBasicBlock(nextEqBlock);
        }
        IrValue eqValue = VisitEqExp(eqExpList.get(eqExpList.size() - 1));
        BranchInstr branchInstr = new BranchInstr(eqValue, trueBlock, falseBlock);

        return eqValue;
    }

    public static IrValue VisitEqExp(EqExp eqExp) {
        ArrayList<RelExp> relExpList = eqExp.GetRelExpList();
        ArrayList<String> opList = eqExp.GetOpList();

        IrValue valueL = VisitRelExp(relExpList.get(0));
        IrValue valueR = null;
        for (int i = 1; i < relExpList.size(); i++) {
            valueR = VisitRelExp(relExpList.get(i));
            // 进行类型转换
            valueL = IrType.ConvertType(valueL, IrBaseType.INT32);
            valueR = IrType.ConvertType(valueR, IrBaseType.INT32);

            valueL = new CompareInstr(opList.get(i - 1), valueL, valueR);
        }
        valueL = IrType.ConvertType(valueL, IrBaseType.INT32);

        CompareInstr compareInstr = new CompareInstr("!=", valueL, new IrConstantInt(0));
        return compareInstr;
    }

    public static IrValue VisitRelExp(RelExp relExp) {
        ArrayList<AddExp> addExpList = relExp.GetAddExpList();
        ArrayList<String> opList = relExp.GetOpList();

        IrValue valueL = VisitAddExp(addExpList.get(0));
        IrValue valueR = null;
        for (int i = 1; i < addExpList.size(); i++) {
            valueR = VisitAddExp(addExpList.get(i));
            // 进行类型转换
            valueL = IrType.ConvertType(valueL, IrBaseType.INT32);
            valueR = IrType.ConvertType(valueR, IrBaseType.INT32);

            valueL = new CompareInstr(opList.get(i - 1), valueL, valueR);
        }
        return valueL;
    }
}
