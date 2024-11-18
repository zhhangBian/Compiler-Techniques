package frontend.ast.value;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.Exp;
import frontend.ast.token.StringConst;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

import java.util.ArrayList;

public class InitVal extends Node {
    public InitVal() {
        super(SyntaxType.INIT_VAL);
    }

    @Override
    public void Parse() {
        // {...} 形式的初值
        if (GetCurrentTokenType().equals(TokenType.LBRACE)) {
            // {
            this.AddNode(new TokenNode());
            // value
            if (!GetCurrentTokenType().equals(TokenType.RBRACE)) {
                // ConstExp
                this.AddNode(new Exp());
                while (GetCurrentTokenType().equals(TokenType.COMMA)) {
                    // ,
                    this.AddNode(new TokenNode());
                    // Exp
                    this.AddNode(new Exp());
                }
            }
            // }
            this.AddNode(new TokenNode());
        }
        // StringConst
        else if (GetCurrentTokenType().equals(TokenType.STRCON)) {
            this.AddNode(new StringConst());
        }
        // ConstExp
        else {
            this.AddNode(new Exp());
        }
    }

    public ArrayList<Exp> GetExpList() {
        ArrayList<Exp> expList = new ArrayList<>();
        for (Node node : this.components) {
            if (node instanceof Exp exp) {
                expList.add(exp);
            }
        }
        return expList;
    }

    // 约定如果是常值形式，那么返回只有一个元素的list
    // 如果是字符串形式，那么是一个阿斯玛的list
    public ArrayList<Integer> GetInitValueList() {
        ArrayList<Integer> initValueList = new ArrayList<>();
        for (Node node : this.components) {
            // exp形式
            if (node instanceof Exp exp) {
                exp.Compute();
                initValueList.add(exp.GetValue());
            }
            // string形式
            else if (node instanceof StringConst stringConst) {
                String string = stringConst.GetStringValue();
                ArrayList<Integer> asciiList = new ArrayList<>();
                for (char c : string.toCharArray()) {
                    asciiList.add((int) c);
                }
                return asciiList;
            }
        }
        return initValueList;
    }

    public String GetInitialString() {
        for (Node node : this.components) {
            // exp形式
            if (node instanceof StringConst stringConst) {
                return stringConst.GetStringValue();
            }
        }
        throw new RuntimeException("no string init");
    }
}
