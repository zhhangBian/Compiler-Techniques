package frontend.ast.stmt;

import error.Error;
import error.ErrorRecorder;
import error.ErrorType;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.block.Block;
import frontend.ast.exp.Cond;
import frontend.ast.exp.Exp;
import frontend.ast.exp.LVal;
import frontend.ast.token.StringConst;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;
import midend.symbol.SymbolManger;

import java.util.ArrayList;

public class Stmt extends Node {
    public enum StmtType {
        BlockStmt,
        IfStmt,
        ForStmt,
        AssignStmt,
        ReturnStmt,
        ExpStmt,
        BreakStmt,
        ContinueStmt,
        GetIntStmt,
        GetChatStmt,
        PrintStmt,
    }

    private StmtType stmtType;

    public Stmt() {
        super(SyntaxType.STMT);
    }

    public StmtType GetStmtType() {
        return this.stmtType;
    }

    @Override
    public void Parse() {
        switch (GetCurrentTokenType()) {
            case LBRACE -> this.ParseBlockStmt();
            case IFTK -> this.ParseIfStmt();
            case FORTK -> this.ParseForStmt();
            case BREAKTK -> this.ParseBreakStmt();
            case CONTINUETK -> this.ParseContinueStmt();
            case RETURNTK -> this.ParseReturnStmt();
            case PRINTFTK -> this.ParsePrintStmt();
            case SEMICN -> this.ParseExpStmt();
            default -> {
                // 预读一个看是否为赋值，否则回溯
                SetBackPoint();
                ErrorRecorder.SetStopRecordError();
                Node node = new Exp();
                node.Parse();

                if (GetCurrentTokenType().equals(TokenType.ASSIGN)) {
                    GoToBackPoint();
                    ErrorRecorder.SetStartRecordError();
                    this.AssignStmt();
                } else {
                    GoToBackPoint();
                    ErrorRecorder.SetStartRecordError();
                    this.ParseExpStmt();
                }
            }
        }
    }

    private void ParseBlockStmt() {
        this.stmtType = StmtType.BlockStmt;
        this.AddNode(new Block());
    }

    private void ParseIfStmt() {
        this.stmtType = StmtType.IfStmt;

        // if
        this.AddNode(new TokenNode());
        // (
        this.AddNode(new TokenNode());
        // Cond
        this.AddNode(new Cond());
        // )
        if (GetCurrentTokenType().equals(TokenType.RPARENT)) {
            this.AddNode(new TokenNode());
        } else {
            this.AddMissRParentError();
        }

        // Stmt
        this.AddNode(new Stmt());

        // 如果有else的话
        if (GetCurrentTokenType().equals(TokenType.ELSETK)) {
            // else
            this.AddNode(new TokenNode());
            // Stmt
            this.AddNode(new Stmt());
        }
    }

    private void ParseForStmt() {
        this.stmtType = StmtType.ForStmt;

        // for
        this.AddNode(new TokenNode());
        // (
        this.AddNode(new TokenNode());

        // ForStmt
        if (!GetCurrentTokenType().equals(TokenType.SEMICN)) {
            this.AddNode(new ForStmt());
        }
        // ;
        this.AddNode(new TokenNode());

        // Cond
        if (!GetCurrentTokenType().equals(TokenType.SEMICN)) {
            this.AddNode(new Cond());
        }
        // ;
        this.AddNode(new TokenNode());

        // ForStmt
        if (!GetCurrentTokenType().equals(TokenType.RPARENT)) {
            this.AddNode(new ForStmt());
        }

        // )
        if (GetCurrentTokenType().equals(TokenType.RPARENT)) {
            this.AddNode(new TokenNode());
        } else {
            this.AddMissRParentError();
        }

        // Stmt
        SymbolManger.EnterForBlock();
        this.AddNode(new Stmt());
        SymbolManger.LeaveForBlock();
    }

    private void ParseBreakStmt() {
        this.stmtType = StmtType.BreakStmt;

        // break
        if (SymbolManger.NotInForBlock()) {
            ErrorRecorder.AddError(new Error(ErrorType.BREAK_OR_CONTINUE_IN_NOT_LOOP,
                this.GetCurrentLineNumber()));
        }
        this.AddNode(new TokenNode());
        // ;
        if (GetCurrentTokenType().equals(TokenType.SEMICN)) {
            this.AddNode(new TokenNode());
        } else {
            this.AddMissSemicnError();
        }
    }

    private void ParseContinueStmt() {
        this.stmtType = StmtType.ContinueStmt;

        // continue
        if (SymbolManger.NotInForBlock()) {
            ErrorRecorder.AddError(new Error(ErrorType.BREAK_OR_CONTINUE_IN_NOT_LOOP,
                this.GetCurrentLineNumber()));
        }
        this.AddNode(new TokenNode());
        // ;
        if (GetCurrentTokenType().equals(TokenType.SEMICN)) {
            this.AddNode(new TokenNode());
        } else {
            this.AddMissSemicnError();
        }
    }

    private void ParseReturnStmt() {
        this.stmtType = StmtType.ReturnStmt;

        // return
        TokenNode tokenNode = new TokenNode();
        tokenNode.Parse();
        this.components.add(tokenNode);
        // Exp
        if (this.IsExpStmt()) {
            this.AddNode(new Exp());
            if (SymbolManger.GetFuncType().equals("void")) {
                ErrorRecorder.AddError(new Error(ErrorType.RETURN_NOT_MATCH, tokenNode.GetLine()));
            }
        }
        // ;
        if (GetCurrentTokenType().equals(TokenType.SEMICN)) {
            this.AddNode(new TokenNode());
        } else {
            this.AddMissSemicnError();
        }
    }

    private void ParsePrintStmt() {
        this.stmtType = StmtType.PrintStmt;

        // printf
        TokenNode printNode = new TokenNode();
        printNode.Parse();
        this.components.add(printNode);
        // (
        this.AddNode(new TokenNode());
        // StringConst
        StringConst node = new StringConst();
        node.Parse();
        this.components.add(node);

        String formatString = node.GetTokenString();
        int formatCount = this.GetFormatStringCount(formatString);
        int realCount = 0;

        while (GetCurrentTokenType().equals(TokenType.COMMA)) {
            // ,
            this.AddNode(new TokenNode());
            // Exp
            this.AddNode(new Exp());
            realCount++;
        }

        if (realCount != formatCount) {
            ErrorRecorder.AddError(new Error(ErrorType.PRINTF_NOT_MATCH, printNode.GetLine()));
        }

        // )
        if (GetCurrentTokenType().equals(TokenType.RPARENT)) {
            this.AddNode(new TokenNode());
        } else {
            this.AddMissRParentError();
        }

        // ;
        if (GetCurrentTokenType().equals(TokenType.SEMICN)) {
            this.AddNode(new TokenNode());
        } else {
            this.AddMissSemicnError();
        }
    }

    private boolean IsExpStmt() {
        return GetCurrentTokenType().equals(TokenType.IDENFR) ||
            GetCurrentTokenType().equals(TokenType.INTCON) ||
            GetCurrentTokenType().equals(TokenType.CHRCON) ||
            GetCurrentTokenType().equals(TokenType.PLUS) ||
            GetCurrentTokenType().equals(TokenType.MINU) ||
            GetCurrentTokenType().equals(TokenType.NOT) ||
            GetCurrentTokenType().equals(TokenType.LPARENT);
    }

    private void ParseExpStmt() {
        this.stmtType = StmtType.ExpStmt;

        if (this.IsExpStmt()) {
            // Exp
            this.AddNode(new Exp());
        }

        if (GetCurrentTokenType().equals(TokenType.SEMICN)) {
            // ;
            this.AddNode(new TokenNode());
        } else {
            this.AddMissSemicnError();
        }
    }

    // 包括了 = exp | getint | getchar 的三种情况
    private void AssignStmt() {
        this.stmtType = StmtType.AssignStmt;

        // LVal
        this.AddNode(new LVal());
        // =
        this.AddNode(new TokenNode());

        // getint | getchar
        if (GetCurrentTokenType().equals(TokenType.GETINTTK) ||
            GetCurrentTokenType().equals(TokenType.GETCHARTK)) {
            this.stmtType = GetCurrentToken().GetStringValue().equals("getint") ?
                StmtType.GetIntStmt : StmtType.GetChatStmt;

            // getint | getchar
            this.AddNode(new TokenNode());
            // (
            this.AddNode(new TokenNode());
            // )
            if (GetCurrentTokenType().equals(TokenType.RPARENT)) {
                this.AddNode(new TokenNode());
            } else {
                this.AddMissRParentError();
            }
        }
        // Exp
        else {
            this.AddNode(new Exp());
        }

        // ;
        if (GetCurrentTokenType().equals(TokenType.SEMICN)) {
            this.AddNode(new TokenNode());
        } else {
            this.AddMissSemicnError();
        }
    }

    @Override
    public void Visit() {
        for (Node component : this.components) {
            if (component instanceof Block) {
                SymbolManger.CreateSonSymbolTable();
                component.Visit();
                SymbolManger.GoToFatherSymbolTable();
            } else {
                component.Visit();
            }
        }
        // 检查是否对const赋值
        this.CheckConstError();
    }

    private void CheckConstError() {
        // 识别赋值语句
        if (this.IsAssignStmt()) {
            // 得到LVal对应的符号
            LVal lval = (LVal) this.components.get(0);
            if (lval.HaveSymbol() && lval.IsConstType()) {
                ErrorRecorder.AddError(new Error(ErrorType.CHANGE_CONST_VALUE, lval.GetLine()));
            }
        }
    }

    private boolean IsAssignStmt() {
        return this.stmtType.equals(StmtType.AssignStmt) ||
            this.stmtType.equals(StmtType.GetIntStmt) ||
            this.stmtType.equals(StmtType.GetChatStmt);
    }

    public boolean IsReturnStmt() {
        return this.stmtType.equals(StmtType.ReturnStmt);
    }

    private int GetFormatStringCount(String formatString) {
        int count = 0;
        for (int i = 0; i < formatString.length() - 1; i++) {
            if (formatString.charAt(i) == '%' &&
                (formatString.charAt(i + 1) == 'd' || formatString.charAt(i + 1) == 'c')) {
                count++;
            }
        }
        return count;
    }

    public Block GetBlockStmtBlock() {
        return (Block) this.components.get(0);
    }

    public boolean ExpStmtHaveExp() {
        return this.stmtType.equals(StmtType.ExpStmt) && this.components.size() > 1;
    }

    public Exp GetExpStmtExp() {
        return (Exp) this.components.get(0);
    }

    public boolean ReturnStmtHaveExp() {
        return this.stmtType.equals(StmtType.ReturnStmt) &&
            this.components.size() > 2 &&
            this.components.get(1) instanceof Exp;
    }

    public Exp GetReturnStmtExp() {
        return (Exp) this.components.get(1);
    }

    public LVal GetLVal() {
        return (LVal) this.components.get(0);
    }

    public Exp GetAssignStmtExp() {
        return (Exp) this.components.get(2);
    }

    public StringConst GetPrintStmtStringConst() {
        return (StringConst) this.components.get(2);
    }

    public ArrayList<Exp> GetPrintStmtExpList() {
        ArrayList<Exp> expList = new ArrayList<>();
        for (Node node : this.components) {
            if (node instanceof Exp exp) {
                expList.add(exp);
            }
        }
        return expList;
    }

    public boolean IfStmtHaveElse() {
        return this.stmtType.equals(StmtType.IfStmt) && this.components.size() > 5;
    }

    public Cond GetIfStmtCond() {
        return (Cond) this.components.get(2);
    }

    public Stmt GetIfStmtIfStmt() {
        return (Stmt) this.components.get(4);
    }

    public Stmt GetIfStmtElseStmt() {
        return (Stmt) this.components.get(6);
    }

    public ForStmt GetForStmtInit() {
        for (int i = 0; i < this.components.size(); i++) {
            if (this.components.get(i) instanceof TokenNode token) {
                if (token.GetSimpleName().equals("(")) {
                    return this.components.get(i + 1) instanceof ForStmt forStmt ? forStmt : null;
                }
            }
        }
        throw new RuntimeException("illegal for stmt");
    }

    public Cond GetForStmtCond() {
        for (int i = 0; i < this.components.size(); i++) {
            if (this.components.get(i) instanceof TokenNode token) {
                // 找到的一定是第一个 ;
                if (token.GetSimpleName().equals(";")) {
                    return this.components.get(i + 1) instanceof Cond cond ? cond : null;
                }
            }
        }
        throw new RuntimeException("illegal for stmt");
    }

    public ForStmt GetForStmtStep() {
        for (int i = 0; i < this.components.size(); i++) {
            if (this.components.get(i) instanceof TokenNode token) {
                if (token.GetSimpleName().equals(")")) {
                    return this.components.get(i - 1) instanceof ForStmt forStmt ? forStmt : null;
                }
            }
        }
        throw new RuntimeException("illegal for stmt");
    }

    public Stmt GetForStmtStmt() {
        return (Stmt) this.components.get(this.components.size() - 1);
    }
}
