package frontend.ast.value;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.exp.ConstExp;
import frontend.ast.token.StringConst;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

import java.util.ArrayList;

public class ConstInitVal extends Node {
    public ConstInitVal() {
        super(SyntaxType.CONST_INIT_VAL);
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
                this.AddNode(new ConstExp());
                while (GetCurrentTokenType().equals(TokenType.COMMA)) {
                    // ,
                    this.AddNode(new TokenNode());
                    // ConstExp
                    this.AddNode(new ConstExp());
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
            this.AddNode(new ConstExp());
        }
    }

    // 约定如果是常值形式，那么返回只有一个元素的list
    // 如果是字符串形式，那么是一个阿斯玛的list
    public ArrayList<Integer> GetInitValueList() {
        ArrayList<Integer> initValueList = new ArrayList<>();
        for (Node node : this.components) {
            if (node instanceof ConstExp constExp) {
                constExp.Compute();
                initValueList.add(constExp.GetValue());
            } else if (node instanceof StringConst stringConst) {
                String string = stringConst.GetStringValue();
                ArrayList<Integer> asciiList = new ArrayList<>();
                for (int i = 0; i < string.length(); i++) {
                    char ch = string.charAt(i);
                    if (ch == '\\' && i < string.length() - 1 && string.charAt(i + 1) == 'n') {
                        asciiList.add((int) ('\n'));
                        i++;
                    } else {
                        asciiList.add((int) ch);
                    }
                }
                return asciiList;
            }
        }
        return initValueList;
    }
}
