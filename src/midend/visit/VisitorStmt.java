package midend.visit;

import frontend.ast.block.Block;
import frontend.ast.exp.Exp;
import frontend.ast.exp.LVal;
import frontend.ast.stmt.Stmt;
import midend.llvm.IrBuilder;
import midend.llvm.constant.IrConstantChar;
import midend.llvm.constant.IrConstantInt;
import midend.llvm.constant.IrConstantString;
import midend.llvm.instr.ReturnInstr;
import midend.llvm.instr.StoreInstr;
import midend.llvm.instr.io.GetCharInstr;
import midend.llvm.instr.io.GetIntInstr;
import midend.llvm.instr.io.PrintCharInstr;
import midend.llvm.instr.io.PrintIntInstr;
import midend.llvm.instr.io.PrintStrInstr;
import midend.llvm.value.IrValue;
import midend.symbol.SymbolManger;

import java.util.ArrayList;

public class VisitorStmt {
    public static void VisitStmt(Stmt stmt) {
        switch (stmt.GetStmtType()) {
            case BlockStmt -> VisitBlockStmt(stmt);
            case ExpStmt -> VisitExpStmt(stmt);
            case AssignStmt -> VisitAssignStmt(stmt);
            case GetIntStmt -> VisitGetIntStmt(stmt);
            case GetChatStmt -> VisitGetCharStmt(stmt);
            case PrintStmt -> VisitPrintStmt(stmt);
            case ReturnStmt -> VisitReturnStmt(stmt);
            case IfStmt -> VisitIfStmt(stmt);
            case ForStmt -> VisitForStmt(stmt);
            case BreakStmt -> VisitBreakStmt(stmt);
            case ContinueStmt -> VisitContinueStmt(stmt);
            default -> throw new RuntimeException("illegal stmt type");
        }
    }

    private static void VisitBlockStmt(Stmt stmt) {
        Block block = stmt.GetBlockStmtBlock();
        SymbolManger.GoToSonSymbolTable();
        VisitorBlock.VisitBlock(block);
        SymbolManger.GoToFatherSymbolTable();
    }

    private static void VisitExpStmt(Stmt stmt) {
        if (stmt.ExpStmtHaveExp()) {
            VisitorExp.VisitExp(stmt.GetExpStmtExp());
        }
    }

    private static void VisitAssignStmt(Stmt stmt) {
        LVal lval = stmt.GetLVal();
        Exp exp = stmt.GetAssignStmtExp();

        IrValue irLVal = VisitorLVal.VisitLVal(lval, true);
        IrValue irExp = VisitorExp.VisitExp(exp);
        StoreInstr storeInstr = new StoreInstr(IrBuilder.GetLocalVarName(), irExp, irLVal);
    }

    private static void VisitGetIntStmt(Stmt stmt) {
        LVal lval = stmt.GetLVal();
        IrValue irLVal = VisitorLVal.VisitLVal(lval, true);
        GetIntInstr getIntInstr = new GetIntInstr(IrBuilder.GetLocalVarName());
        StoreInstr storeInstr = new StoreInstr(IrBuilder.GetLocalVarName(), irLVal, getIntInstr);
    }

    private static void VisitGetCharStmt(Stmt stmt) {
        LVal lval = stmt.GetLVal();
        IrValue irLVal = VisitorLVal.VisitLVal(lval, true);
        GetCharInstr getIntInstr = new GetCharInstr(IrBuilder.GetLocalVarName());
        StoreInstr storeInstr = new StoreInstr(IrBuilder.GetLocalVarName(), irLVal, getIntInstr);
    }

    private static void VisitPrintStmt(Stmt stmt) {
        String formatString = stmt.GetPrintStmtStringConst().GetStringValue();
        ArrayList<Exp> expList = stmt.GetPrintStmtExpList();
        int expCnt = 0;

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < formatString.length(); i++) {
            // 格式输出
            if (formatString.charAt(i) == '%') {
                // 遇到格式输出，先将之前的常量输出
                if (!builder.isEmpty()) {
                    IrConstantString irConstantString =
                        IrBuilder.GetNewIrConstantString(builder.toString());

                    PrintStrInstr printStrInstr =
                        new PrintStrInstr(IrBuilder.GetLocalVarName(), irConstantString);
                    builder.setLength(0);
                }

                if (formatString.charAt(i + 1) == 'd') {
                    IrValue irValue = VisitorExp.VisitExp(expList.get(expCnt++));
                    PrintIntInstr printIntInstr =
                        new PrintIntInstr(IrBuilder.GetLocalVarName(), irValue);

                    i++;
                } else if (formatString.charAt(i + 1) == 'c') {
                    IrValue irValue = VisitorExp.VisitExp(expList.get(expCnt++));
                    PrintCharInstr printCharInstr =
                        new PrintCharInstr(IrBuilder.GetLocalVarName(), irValue);

                    i++;
                }
            }
            // 转义只会是换行
            else if (formatString.charAt(i) == '\\') {
                builder.append("\\n");
                i++;
            }
            // 一般的字符
            else {
                builder.append(formatString.charAt(i));
            }
        }
        // 还有剩余字符
        if (!builder.isEmpty()) {
            IrConstantString irConstantString =
                IrBuilder.GetNewIrConstantString(builder.toString());
            PrintStrInstr printStrInstr =
                new PrintStrInstr(IrBuilder.GetLocalVarName(), irConstantString);
        }
    }

    private static void VisitIfStmt(Stmt stmt) {
        // TODO
    }

    private static void VisitReturnStmt(Stmt stmt) {
        IrValue irReturn = null;
        if (stmt.ReturnStmtHaveExp()) {
            irReturn = VisitorExp.VisitExp(stmt.GetReturnStmtExp());
        } else if (IrBuilder.GetCurrentFunctionReturnType().IsInt32Type()) {
            irReturn = new IrConstantInt(0);
        } else if (IrBuilder.GetCurrentFunctionReturnType().IsInt8Type()) {
            irReturn = new IrConstantChar(0);
        }

        ReturnInstr returnInstr = new ReturnInstr(IrBuilder.GetLocalVarName(), irReturn);
    }

    private static void VisitForStmt(Stmt stmt) {
        // TODO
    }

    private static void VisitBreakStmt(Stmt stmt) {
        // TODO
    }

    private static void VisitContinueStmt(Stmt stmt) {
        // TODO
    }
}
