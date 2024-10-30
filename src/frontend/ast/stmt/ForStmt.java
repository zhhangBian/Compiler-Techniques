package frontend.ast.stmt;

import error.Error;
import error.ErrorRecorder;
import error.ErrorType;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.Exp;
import frontend.ast.exp.LVal;
import frontend.ast.token.TokenNode;

public class ForStmt extends Node {
    public ForStmt() {
        super(SyntaxType.FOR_STMT);
    }

    @Override
    public void Parse() {
        // LVal
        this.AddNode(new LVal());
        // =
        this.AddNode(new TokenNode());
        // Exp
        this.AddNode(new Exp());
    }

    @Override
    public void Visit() {
        super.Visit();

        // 识别赋值语句
        if (this.components.get(0) instanceof LVal lVal &&
            this.components.get(1) instanceof TokenNode tokenNode &&
            tokenNode.GetTokenString().equals("=")) {
            // 得到LVal对应的符号
            if (lVal.HaveSymbol() && lVal.CannotChangeValue()) {
                ErrorRecorder.AddError(new Error(ErrorType.CHANGE_CONST_VALUE, lVal.GetLine()));
            }
        }
    }
}
