package frontend.ast;

import frontend.ast.decl.Decl;
import frontend.lexer.TokenType;

import java.util.ArrayList;

public class CompUnit extends Node {
    public CompUnit() {
        super(SyntaxType.COMP_UNIT, new ArrayList<>());
    }

    @Override
    public void Parse() {
        while (GetCurrentToken() != null) {
            Node node;
            // 主成分
            if (Peek(1).GetTokenType().equals(TokenType.MAINTK)) {
                node = new MainFuncDef();
            }
            // 函数
            else if (Peek(2).GetTokenType().equals(TokenType.LPARENT)) {
                node = new FuncDef();
            }
            // 常量
            else if (GetCurrentToken().GetTokenType().equals(TokenType.CONSTTK) ||
                GetCurrentToken().GetTokenType().equals(TokenType.INTTK) ||
                GetCurrentToken().GetTokenType().equals(TokenType.CHARTK)) {
                node = new Decl();
            }
            // 未定义
            else {
                break;
            }

            node.Parse();
            this.components.add(node);
        }
    }
}
