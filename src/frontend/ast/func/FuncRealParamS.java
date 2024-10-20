package frontend.ast.func;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.Exp;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

import java.util.ArrayList;

public class FuncRealParamS extends Node {
    public FuncRealParamS() {
        super(SyntaxType.FUNC_REAL_PARAM_S);
    }

    @Override
    public void Parse() {
        this.AddNode(new Exp());
        while (GetCurrentTokenType().equals(TokenType.COMMA)) {
            // ,
            this.AddNode(new TokenNode());
            // Exp
            this.AddNode(new Exp());
        }
    }

    public ArrayList<Exp> GetRealParamList() {
        ArrayList<Exp> realParamList = new ArrayList<>();
        for (Node component : this.components) {
            if (component instanceof Exp exp) {
                realParamList.add(exp);
            }
        }
        return realParamList;
    }
}
