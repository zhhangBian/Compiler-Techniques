package midend.visit;

import frontend.ast.decl.ConstDecl;
import frontend.ast.decl.ConstDef;
import frontend.ast.decl.Decl;
import frontend.ast.decl.VarDecl;
import frontend.ast.decl.VarDef;
import frontend.ast.exp.Exp;
import midend.llvm.IrBuilder;
import midend.llvm.constant.IrConstant;
import midend.llvm.constant.IrConstantArray;
import midend.llvm.constant.IrConstantChar;
import midend.llvm.constant.IrConstantInt;
import midend.llvm.instr.AllocateInstr;
import midend.llvm.instr.GepInstr;
import midend.llvm.instr.StoreInstr;
import midend.llvm.type.IrArrayType;
import midend.llvm.type.IrBaseType;
import midend.llvm.type.IrPointerType;
import midend.llvm.type.IrType;
import midend.llvm.value.IrGlobalValue;
import midend.llvm.value.IrValue;
import midend.symbol.SymbolManger;
import midend.symbol.SymbolType;
import midend.symbol.ValueSymbol;

import java.util.ArrayList;

public class VisitorDecl {
    public static void VisitDecl(Decl decl) {
        if (decl.IsConstDecl()) {
            VisitConstDecl((ConstDecl) decl.GetDecl());
        } else {
            VisitVarDecl((VarDecl) decl.GetDecl());
        }
    }

    private static void VisitConstDecl(ConstDecl constDecl) {
        ArrayList<ConstDef> constDefs = constDecl.GetConstDef();
        for (ConstDef constDef : constDefs) {
            VisitConstDef(constDef);
        }
    }

    private static void VisitVarDecl(VarDecl varDecl) {
        ArrayList<VarDef> varDefs = varDecl.GetVAeDefs();
        for (VarDef varDef : varDefs) {
            VisitVarDef(varDef);
        }
    }

    private static void VisitConstDef(ConstDef constDef) {
        ValueSymbol symbol =
            (ValueSymbol) SymbolManger.GetSymbol(constDef.GetIdent().GetSimpleName());
        // 当前在global层级
        if (SymbolManger.IsGlobal()) {
            IrGlobalValue irGlobalValue = IrBuilder.GetNewIrGlobalValue(
                new IrPointerType(GetSymbolIrType(symbol)), GetValueConstant(symbol));
            symbol.SetIrValue(irGlobalValue);
        }
        // 局部变量
        else {
            AllocateInstr allocateInstr = new AllocateInstr(GetSymbolIrType(symbol));
            symbol.SetIrValue(allocateInstr);

            ArrayList<Integer> initValueList = symbol.GetInitValueList();
            // 如果不是数组，添加存储指令：建立空间、赋初值
            if (symbol.GetDimension() == 0) {
                StoreInstr storeInstr = new StoreInstr(GetValueConstant(symbol), allocateInstr);
            } else {
                SymbolType symbolType = symbol.GetSymbolType();

                for (int i = 0; i < initValueList.size(); i++) {
                    // 计算偏移值
                    GepInstr gepInstr = new GepInstr(allocateInstr, new IrConstantInt(i));
                    IrValue initValue = symbolType.equals(SymbolType.CONST_CHAR_ARRAY) ?
                        new IrConstantChar(initValueList.get(i)) :
                        new IrConstantInt(initValueList.get(i));
                    // 将初始值存储到偏移量中
                    StoreInstr storeInstr = new StoreInstr(initValue, gepInstr);
                }
            }
        }
    }

    private static void VisitVarDef(VarDef varDef) {
        ValueSymbol symbol =
            (ValueSymbol) SymbolManger.GetSymbol(varDef.GetIdent().GetSimpleName());
        // 当前在global层级
        if (SymbolManger.IsGlobal()) {
            IrGlobalValue irGlobalValue = IrBuilder.GetNewIrGlobalValue(
                new IrPointerType(GetSymbolIrType(symbol)), GetValueConstant(symbol));
            symbol.SetIrValue(irGlobalValue);

            IrConstant irConstant = GetValueConstant(symbol);
        }
        // 局部变量
        else {
            VisitLocalVarDef(varDef, symbol);
        }
    }

    private static void VisitLocalVarDef(VarDef varDef, ValueSymbol symbol) {
        AllocateInstr allocateInstr = new AllocateInstr(GetSymbolIrType(symbol));
        symbol.SetIrValue(allocateInstr);

        // 如果不是数组，添加存储指令：建立空间、赋初值
        if (symbol.GetDimension() == 0) {
            // 如果有初始值，给初值
            if (varDef.HaveInitVal()) {
                Exp exp = varDef.GetInitVal().GetExpList().get(0);
                IrValue irExp = VisitorExp.VisitExp(exp);

                if (symbol.GetSymbolType().equals(SymbolType.CHAR)) {
                    irExp = IrType.ConvertType(irExp, IrBaseType.INT8);
                }
                StoreInstr storeInstr = new StoreInstr(irExp, allocateInstr);
            }
        } else {
            if (varDef.HaveInitVal()) {
                if (symbol.GetSymbolType().equals(SymbolType.CHAR_ARRAY)) {
                    String initialString = varDef.GetInitVal().GetInitialString();
                    for (int i = 0; i < initialString.length(); i++) {
                        // 计算偏移值
                        GepInstr gepInstr = new GepInstr(allocateInstr, new IrConstantInt(i));
                        // 将初始值存储到偏移量中
                        StoreInstr storeInstr = new StoreInstr(
                            new IrConstantChar(initialString.charAt(i)), gepInstr);
                    }
                } else {
                    // 生成一系列GEP+store指令，将初始值存入常量
                    ArrayList<Exp> expList = varDef.GetInitVal().GetExpList();
                    for (int i = 0; i < expList.size(); i++) {
                        IrValue irExp = VisitorExp.VisitExp(expList.get(i));
                        // 计算偏移值
                        GepInstr gepInstr = new GepInstr(allocateInstr, new IrConstantInt(i));
                        // 将初始值存储到偏移量中
                        StoreInstr storeInstr = new StoreInstr(irExp, gepInstr);
                    }
                }
            }
        }
    }

    private static IrType GetSymbolIrType(ValueSymbol symbol) {
        return switch (symbol.GetSymbolType()) {
            case CHAR, CONST_CHAR -> IrBaseType.INT8;
            case INT, CONST_INT -> IrBaseType.INT32;
            case CHAR_ARRAY, CONST_CHAR_ARRAY ->
                new IrArrayType(symbol.GetTotalDepth(), IrBaseType.INT8);
            case INT_ARRAY, CONST_INT_ARRAY ->
                new IrArrayType(symbol.GetTotalDepth(), IrBaseType.INT32);
            default -> throw new RuntimeException("now fit value type for global value");
        };
    }

    private static IrConstant GetValueConstant(ValueSymbol symbol) {
        ArrayList<Integer> initValueList = symbol.GetInitValueList();
        // 非数组类型
        if (symbol.GetDimension() == 0) {
            if (initValueList.isEmpty()) {
                initValueList.add(0);
            }
            int initValue = initValueList.get(0);
            return GetSymbolIrType(symbol).equals(IrBaseType.INT8) ?
                new IrConstantChar(initValue) : new IrConstantInt(initValue);
        }
        // 数组类型
        else {
            IrArrayType irArrayType = (IrArrayType) GetSymbolIrType(symbol);
            IrType irType = irArrayType.GetElementType();
            ArrayList<IrConstant> constantList = new ArrayList<>();
            if (irType.IsInt8Type()) {
                for (Integer num : initValueList) {
                    constantList.add(new IrConstantChar(num));
                }
            } else {
                for (Integer num : initValueList) {
                    constantList.add(new IrConstantInt(num));
                }
            }
            return new IrConstantArray(symbol.GetTotalDepth(), irType,
                symbol.GetSymbolName(), constantList);
        }
    }
}
