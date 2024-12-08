package midend.visit;

import frontend.ast.block.Block;
import frontend.ast.exp.Cond;
import frontend.ast.exp.Exp;
import frontend.ast.exp.LVal;
import frontend.ast.stmt.ForStmt;
import frontend.ast.stmt.Stmt;
import midend.llvm.IrBuilder;
import midend.llvm.constant.IrConstantChar;
import midend.llvm.constant.IrConstantInt;
import midend.llvm.constant.IrConstantString;
import midend.llvm.instr.JumpInstr;
import midend.llvm.instr.ReturnInstr;
import midend.llvm.instr.StoreInstr;
import midend.llvm.instr.io.GetCharInstr;
import midend.llvm.instr.io.GetIntInstr;
import midend.llvm.instr.io.PrintCharInstr;
import midend.llvm.instr.io.PrintIntInstr;
import midend.llvm.instr.io.PrintStrInstr;
import midend.llvm.type.IrBaseType;
import midend.llvm.type.IrPointerType;
import midend.llvm.type.IrType;
import midend.llvm.value.IrBasicBlock;
import midend.llvm.value.IrLoop;
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
            case ForStmt -> VisitStmtForStmt(stmt);
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
        irExp = IrType.ConvertType(irExp, ((IrPointerType) irLVal.GetIrType()).GetTargetType());
        StoreInstr storeInstr = new StoreInstr(irExp, irLVal);
    }

    private static void VisitGetIntStmt(Stmt stmt) {
        LVal lval = stmt.GetLVal();
        IrValue irLVal = VisitorLVal.VisitLVal(lval, true);
        GetIntInstr getIntInstr = new GetIntInstr();
        StoreInstr storeInstr = new StoreInstr(getIntInstr, irLVal);
    }

    private static void VisitGetCharStmt(Stmt stmt) {
        LVal lval = stmt.GetLVal();
        IrValue irLVal = VisitorLVal.VisitLVal(lval, true);
        GetCharInstr getCharInstr = new GetCharInstr();
        IrValue charValue = IrType.ConvertType(getCharInstr, IrBaseType.INT8);
        StoreInstr storeInstr = new StoreInstr(charValue, irLVal);
    }

    private static void VisitPrintStmt(Stmt stmt) {
        String formatString = stmt.GetPrintStmtStringConst().GetStringValue();
        ArrayList<Exp> expList = stmt.GetPrintStmtExpList();
        int expCnt = 0;

        // 先解析参数
        ArrayList<IrValue> printValueList = new ArrayList<>();
        for (Exp exp : expList) {
            printValueList.add(VisitorExp.VisitExp(exp));
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < formatString.length(); i++) {
            // 格式输出
            if (formatString.charAt(i) == '%') {
                // 遇到格式输出，先将之前的常量输出
                if (!builder.isEmpty()) {
                    IrConstantString irConstantString =
                        IrBuilder.GetNewIrConstantString(builder.toString());

                    PrintStrInstr printStrInstr = new PrintStrInstr(irConstantString);
                    builder.setLength(0);
                }

                if (formatString.charAt(i + 1) == 'd') {
                    IrValue irValue = printValueList.get(expCnt++);
                    irValue = IrType.ConvertType(irValue, IrBaseType.INT32);
                    PrintIntInstr printIntInstr = new PrintIntInstr(irValue);

                    i++;
                } else if (formatString.charAt(i + 1) == 'c') {
                    IrValue irValue = printValueList.get(expCnt++);
                    // 输出时进行类型转换
                    IrValue printValue = IrType.ConvertType(irValue, IrBaseType.INT8);
                    PrintCharInstr printCharInstr = new PrintCharInstr(printValue);

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
            PrintStrInstr printStrInstr = new PrintStrInstr(irConstantString);
        }
    }

    private static void VisitIfStmt(Stmt stmt) {
        Cond cond = stmt.GetIfStmtCond();
        Stmt ifStmt = stmt.GetIfStmtIfStmt();
        // if条件为真进入的block
        IrBasicBlock ifBlock = IrBuilder.GetNewBasicBlockIr();

        // 有else的情况
        if (stmt.IfStmtHaveElse()) {
            // 获取block块
            IrBasicBlock elseBlock = IrBuilder.GetNewBasicBlockIr();

            // 解析Cond部分
            VisitorExp.VisitCond(cond, ifBlock, elseBlock);
            JumpInstr ifJump = new JumpInstr(ifBlock);

            // 解析if部分
            IrBuilder.SetCurrentBasicBlock(ifBlock);
            VisitStmt(ifStmt);

            IrBasicBlock followBlock = IrBuilder.GetNewBasicBlockIr();
            JumpInstr followJump = new JumpInstr(followBlock);

            // 解析else部分
            Stmt elseStmt = stmt.GetIfStmtElseStmt();
            IrBuilder.SetCurrentBasicBlock(elseBlock);
            VisitStmt(elseStmt);
            JumpInstr elseJump = new JumpInstr(followBlock);

            // 设置follow部分
            IrBuilder.SetCurrentBasicBlock(followBlock);
        }
        // 无else的情况
        else {
            // 获取block块
            IrBasicBlock followBlock = IrBuilder.GetNewBasicBlockIr();

            // 解析Cond部分
            VisitorExp.VisitCond(cond, ifBlock, followBlock);
            JumpInstr ifJump = new JumpInstr(ifBlock);

            // 解析if部分
            IrBuilder.SetCurrentBasicBlock(ifBlock);
            VisitStmt(ifStmt);
            JumpInstr followJump = new JumpInstr(followBlock);

            // 设置follow部分
            IrBuilder.SetCurrentBasicBlock(followBlock);
        }
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

        if (irReturn != null) {
            irReturn = IrType.ConvertType(irReturn, IrBuilder.GetCurrentFunctionReturnType());
        }
        ReturnInstr returnInstr = new ReturnInstr(irReturn);
    }

    private static void VisitStmtForStmt(Stmt stmt) {
        // 为Cond单独创建一个基本块
        final IrBasicBlock condBlock = IrBuilder.GetNewBasicBlockIr();
        final IrBasicBlock bodyBlock = IrBuilder.GetNewBasicBlockIr();
        final IrBasicBlock stepBlock = IrBuilder.GetNewBasicBlockIr();
        final IrBasicBlock followBlock = IrBuilder.GetNewBasicBlockIr();

        // 将loop入栈：方便分析continue和break
        IrBuilder.LoopStackPush(new IrLoop(condBlock, bodyBlock, stepBlock, followBlock));

        // 解析初始化stmt
        ForStmt forStmtInit = stmt.GetForStmtInit();
        if (forStmtInit != null) {
            VisitForStmt(forStmtInit);
        }
        JumpInstr jumpCond = new JumpInstr(condBlock);

        // 解析条件cond
        IrBuilder.SetCurrentBasicBlock(condBlock);
        Cond cond = stmt.GetForStmtCond();
        if (cond != null) {
            VisitorExp.VisitCond(cond, bodyBlock, followBlock);
        }

        // 处理for循环体
        IrBuilder.SetCurrentBasicBlock(bodyBlock);
        // 解析循环函数体stmt
        Stmt bodyStmt = stmt.GetForStmtStmt();
        VisitStmt(bodyStmt);
        JumpInstr jumpStep = new JumpInstr(stepBlock);
        // 解析步进stmt
        IrBuilder.SetCurrentBasicBlock(stepBlock);
        ForStmt stepStmt = stmt.GetForStmtStep();
        if (stepStmt != null) {
            VisitForStmt(stepStmt);
        }
        // 进行跳转
        JumpInstr jumpBack = new JumpInstr(condBlock);

        // loop出栈
        IrBuilder.LoopStackPop();

        // 处理后续块
        IrBuilder.SetCurrentBasicBlock(followBlock);
    }

    public static void VisitForStmt(ForStmt forStmt) {
        LVal lval = forStmt.GetLVal();
        Exp exp = forStmt.GetExp();

        IrValue irLVal = VisitorLVal.VisitLVal(lval, true);
        IrValue irExp = VisitorExp.VisitExp(exp);
        irExp = IrType.ConvertType(irExp, ((IrPointerType) irLVal.GetIrType()).GetTargetType());
        StoreInstr storeInstr = new StoreInstr(irExp, irLVal);
    }

    private static void VisitBreakStmt(Stmt stmt) {
        JumpInstr jumpInstr = new JumpInstr(IrBuilder.LoopStackPeek().GetFollowBlock());
    }

    private static void VisitContinueStmt(Stmt stmt) {
        JumpInstr jumpInstr = new JumpInstr(IrBuilder.LoopStackPeek().GetStepBlock());
    }
}
