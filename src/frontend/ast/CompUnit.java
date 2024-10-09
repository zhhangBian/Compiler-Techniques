package frontend.ast;

import frontend.lexer.TokenStream;
import frontend.lexer.TokenType;

import java.util.ArrayList;

public class CompUnit extends Node {
    public CompUnit(TokenStream tokenStream) {
        super(SyntaxType.COMP_UNIT, new ArrayList<>(), tokenStream);
    }

    @Override
    public void Parse() {
        while (this.tokenStream.GetCurrentToken() != null) {
            // 偷看来识别语法成分

            // 主成分
            if (this.tokenStream.Peek(1).GetTokenType().equals(TokenType.MAINTK)) {
                Node node = new MainFuncDef();
                node.Parse();
                this.components.add(node);
            }
            // 函数
            else if (this.tokenStream.Peek(2).GetTokenType().equals(TokenType.LPARENT)) {
                Node node = new FuncDef();
                node.Parse();
                this.components.add(node);
            }
            // 常量

            // 否则为一般定义

            // 未定义
        }
    }
}
