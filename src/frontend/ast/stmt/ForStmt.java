package frontend.ast.stmt;

import error.Error;
import error.ErrorRecorder;
import error.ErrorType;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.decl.VarDef;
import frontend.ast.exp.Exp;
import frontend.ast.exp.LVal;
import frontend.ast.token.BType;
import frontend.ast.token.TokenNode;

public class ForStmt extends Node {
    private boolean isDecl;

    public ForStmt() {
        super(SyntaxType.FOR_STMT);
    }

    @Override
    public void Parse() {
        String token = this.GetCurrentToken().GetStringValue();
        if (token.equals("int") || token.equals("char")) {
            this.AddNode(new BType());
            this.AddNode(new VarDef());
            System.out.println("add var def");
            this.isDecl = true;
        } else {
            // LVal
            this.AddNode(new LVal());
            // =
            this.AddNode(new TokenNode());
            // Exp
            this.AddNode(new Exp());

            this.isDecl = false;
        }
    }

    @Override
    public void Visit() {
        if (this.IsDecl()) {
            // int | char
            BType type = (BType) this.components.get(0);
            String typeString = type.GetTokenString();

            for (Node component : this.components) {
                if (component instanceof VarDef varDef) {
                    varDef.SetTypeString(typeString);
                }
                component.Visit();
            }
        }
        // 一般赋值语句
        else {
            super.Visit();
            // 识别赋值语句
            if (this.components.get(0) instanceof LVal lVal &&
                this.components.get(1) instanceof TokenNode tokenNode &&
                tokenNode.GetTokenString().equals("=")) {
                // const类型不能修改值
                if (lVal.HaveSymbol() && lVal.IsConstType()) {
                    ErrorRecorder.AddError(new Error(ErrorType.CHANGE_CONST_VALUE, lVal.GetLine()));
                }
            }
        }
    }

    public LVal GetLVal() {
        return (LVal) this.components.get(0);
    }

    public Exp GetExp() {
        return (Exp) this.components.get(2);
    }

    public boolean IsDecl() {
        return this.isDecl;
    }

    public VarDef GetVarDef() {
        return (VarDef) this.components.get(1);
    }
}
