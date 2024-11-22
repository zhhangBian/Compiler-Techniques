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
            int initSize = initValueList.size();
            int depth = symbol.GetTotalDepth();
            for (int i = initSize; i < depth; i++) {
                initValueList.add(0);
            }
            // 如果不是数组，添加存储指令：建立空间、赋初值
            if (symbol.GetDimension() == 0) {
                StoreInstr storeInstr = new StoreInstr(GetValueConstant(symbol), allocateInstr);
            } else {
                SymbolType symbolType = symbol.GetSymbolType();

                for (int i = 0; i < initValueList.size(); i++) {
                    if (symbolType.equals(SymbolType.CONST_CHAR_ARRAY)) {
                        char ch = (char) initValueList.get(i).intValue();
                        if (ch == '\\' && i < initValueList.size() - 1 &&
                            (char) initValueList.get(i + 1).intValue() == 'n') {
                            ch = '\n';
                            i++;
                        }
                        // 计算偏移值
                        GepInstr gepInstr = new GepInstr(allocateInstr, new IrConstantChar(i));
                        IrValue initValue = new IrConstantChar(ch);
                        // 将初始值存储到偏移量中
                        StoreInstr storeInstr = new StoreInstr(initValue, gepInstr);
                    } else {
                        // 计算偏移值
                        GepInstr gepInstr = new GepInstr(allocateInstr, new IrConstantInt(i));
                        IrValue initValue = new IrConstantInt(initValueList.get(i));
                        // 将初始值存储到偏移量中
                        StoreInstr storeInstr = new StoreInstr(initValue, gepInstr);
                    }
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
        }
        // 局部变量
        else {
            VisitLocalVarDef(varDef, symbol);
        }
    }

    private static void VisitLocalVarDef(VarDef varDef, ValueSymbol symbol) {
        AllocateInstr allocateInstr = new AllocateInstr(GetSymbolIrType(symbol));

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
                    // 字符串形式的初始值
                    if (varDef.GetInitVal().HaveStringConst()) {
                        String initialString = varDef.GetInitVal().GetStringConst();
                        for (int i = 0; i < initialString.length(); i++) {
                            char ch = initialString.charAt(i);
                            if (ch == '\\' && i < initialString.length() - 1 &&
                                initialString.charAt(i + 1) == 'n') {
                                ch = '\n';
                                i++;
                            }

                            // 计算偏移值
                            GepInstr gepInstr = new GepInstr(allocateInstr, new IrConstantChar(i));
                            // 将初始值存储到偏移量中
                            StoreInstr storeInstr =
                                new StoreInstr(new IrConstantChar(ch), gepInstr);
                        }
                        for (int i = initialString.length(); i < symbol.GetTotalDepth(); i++) {
                            // 存一个0进去
                            GepInstr gepInstr = new GepInstr(allocateInstr, new IrConstantChar(i));
                            StoreInstr storeInstr =
                                new StoreInstr(new IrConstantChar(0), gepInstr);
                        }
                    }
                    // 字符列表形式的初始值
                    else {
                        // 生成一系列GEP+store指令，将初始值存入常量
                        ArrayList<Exp> expList = varDef.GetInitVal().GetExpList();
                        for (int i = 0; i < expList.size(); i++) {
                            IrValue irExp = VisitorExp.VisitExp(expList.get(i));
                            irExp = IrType.ConvertType(irExp, IrBaseType.INT8);
                            // 计算偏移值
                            GepInstr gepInstr = new GepInstr(allocateInstr, new IrConstantChar(i));
                            // 将初始值存储到偏移量中
                            StoreInstr storeInstr = new StoreInstr(irExp, gepInstr);
                        }
                        for (int i = expList.size(); i < symbol.GetTotalDepth(); i++) {
                            // 存一个0进去
                            GepInstr gepInstr = new GepInstr(allocateInstr, new IrConstantChar(i));
                            StoreInstr storeInstr =
                                new StoreInstr(new IrConstantChar(0), gepInstr);
                        }
                    }
                } else {
                    // 生成一系列GEP+store指令，将初始值存入常量
                    ArrayList<Exp> expList = varDef.GetInitVal().GetExpList();
                    for (int i = 0; i < expList.size(); i++) {
                        IrValue irExp = VisitorExp.VisitExp(expList.get(i));
                        irExp = IrType.ConvertType(irExp, IrBaseType.INT32);
                        // 计算偏移值
                        GepInstr gepInstr = new GepInstr(allocateInstr, new IrConstantInt(i));
                        // 将初始值存储到偏移量中
                        StoreInstr storeInstr = new StoreInstr(irExp, gepInstr);
                    }
                }
            }
        }
        symbol.SetIrValue(allocateInstr);
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
            int totalDepth = symbol.GetTotalDepth();
            IrType irType = irArrayType.GetElementType();
            ArrayList<IrConstant> constantList = new ArrayList<>();
            if (irType.IsInt8Type()) {
                for (Integer num : initValueList) {
                    constantList.add(new IrConstantChar(num));
                }
                for (int i = initValueList.size(); i < totalDepth; i++) {
                    constantList.add(new IrConstantChar(0));
                }
            } else {
                for (Integer num : initValueList) {
                    constantList.add(new IrConstantInt(num));
                }
                for (int i = initValueList.size(); i < totalDepth; i++) {
                    constantList.add(new IrConstantInt(0));
                }
            }

            return new IrConstantArray(symbol.GetTotalDepth(), irType,
                symbol.GetSymbolName(), constantList);
        }
    }
}
