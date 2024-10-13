package frontend.ast.stmt;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.block.Block;
import frontend.ast.exp.Cond;
import frontend.ast.exp.Exp;
import frontend.ast.exp.LVal;
import frontend.ast.token.StringConst;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

public class Stmt extends Node {
    public Stmt() {
        super(SyntaxType.STMT);
    }

    @Override
    public void Parse() {
        switch (GetCurrentTokenType()) {
            case LBRACE:
                this.BlockStmt();
                break;
            case IFTK:
                this.IfStmt();
                break;
            case FORTK:
                this.ForStmt();
                break;
            case BREAKTK:
                this.BreakStmt();
                break;
            case CONTINUETK:
                this.ContinueStmt();
                break;
            case RETURNTK:
                this.ReturnStmt();
                break;
            case PRINTFTK:
                this.PrintStmt();
                break;
            case SEMICN:
                this.ExpStmt();
                break;
            default:
                // 预读一个看是否为赋值，否则回溯
                SetBackPoint();
                Node node = new Exp();
                node.Parse();

                if (GetCurrentTokenType().equals(TokenType.ASSIGN)) {
                    GoToBackPoint();
                    this.AssignStmt();
                }
                else {
                    GoToBackPoint();
                    this.ExpStmt();
                }
                break;
        }
    }

    private void BlockStmt() {
        this.AddNode(new Block());
    }

    private void IfStmt() {
        // if
        this.AddNode(new TokenNode());
        // (
        this.AddNode(new TokenNode());
        // Cond
        this.AddNode(new Cond());
        // )
        if (GetCurrentTokenType().equals(TokenType.RPARENT)) {
            this.AddNode(new TokenNode());
        }
        // TODO：错误处理

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

    private void ForStmt() {
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
        }
        // TODO：错误处理

        // Stmt
        this.AddNode(new Stmt());
    }

    private void BreakStmt() {
        // break
        this.AddNode(new TokenNode());
        // ;
        this.AddNode(new TokenNode());
    }

    private void ContinueStmt() {
        // continue
        this.AddNode(new TokenNode());
        // ;
        this.AddNode(new TokenNode());
    }

    private void ReturnStmt() {
        // return
        this.AddNode(new TokenNode());
        // Exp
        if (this.IsExpStmt()) {
            this.AddNode(new Exp());
        }
        // ;
        if (GetCurrentTokenType().equals(TokenType.SEMICN)) {
            this.AddNode(new TokenNode());
        }
        // TODO：错误处理
    }

    private void PrintStmt() {
        // printf
        this.AddNode(new TokenNode());
        // (
        this.AddNode(new TokenNode());
        // StringConst
        this.AddNode(new StringConst());

        while (GetCurrentTokenType().equals(TokenType.COMMA)) {
            // ,
            this.AddNode(new TokenNode());
            // Exp
            this.AddNode(new Exp());
        }

        // )
        this.AddNode(new TokenNode());
        // TODO：错误处理
        // ;
        this.AddNode(new TokenNode());
        // TODO：错误处理
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

    private void ExpStmt() {
        if (this.IsExpStmt()) {
            // Exp
            this.AddNode(new Exp());
        }

        if (GetCurrentTokenType().equals(TokenType.SEMICN)) {
            // ;
            this.AddNode(new TokenNode());
        }
        // TODO：错误处理
    }

    private void AssignStmt() {
        // LVal
        this.AddNode(new LVal());
        // =
        this.AddNode(new TokenNode());

        // getint | getchar
        if (GetCurrentTokenType().equals(TokenType.GETINTTK) ||
            GetCurrentTokenType().equals(TokenType.GETCHARTK)) {
            // getint | getchar
            this.AddNode(new TokenNode());
            // (
            this.AddNode(new TokenNode());
            // )
            if (GetCurrentTokenType().equals(TokenType.RPARENT)) {
                this.AddNode(new TokenNode());
            }
            // TODO：错误处理
        }
        // assign
        else {
            this.AddNode(new Exp());
        }

        // ;
        if (GetCurrentTokenType().equals(TokenType.SEMICN)) {
            this.AddNode(new TokenNode());
        }
        // TODO：错误处理
    }
}
