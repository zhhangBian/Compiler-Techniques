package frontend.ast;

import frontend.lexer.TokenType;

import java.util.ArrayList;

public class CompUnit extends Node {
    public CompUnit() {
        super(SyntaxType.COMP_UNIT, new ArrayList<>());
    }

    @Override
    public void Parse() {
        while (GetCurrentToken() != null) {
            // 主成分
            if (Peek(1).GetTokenType().equals(TokenType.MAINTK)) {
                Node node = new MainFuncDef();
                node.Parse();
                this.components.add(node);
            }
            // 函数
            else if (Peek(2).GetTokenType().equals(TokenType.LPARENT)) {
                Node node = new FuncDef();
                node.Parse();
                this.components.add(node);
            }
            // 常量
            else if (GetCurrentToken().GetTokenType().equals(TokenType.CONSTTK)) {
                Node node = new ConstDecl();
                node.Parse();
                this.components.add(node);
            }
            // 否则为一般定义
            else if (GetCurrentToken().GetTokenType().equals(TokenType.INTTK) ||
                GetCurrentToken().GetTokenType().equals(TokenType.CHARTK)) {
                Node node = new VarDecl();
                node.Parse();
                this.components.add(node);
            }
            // 未定义
            else {
                break;
            }
        }
    }
}
