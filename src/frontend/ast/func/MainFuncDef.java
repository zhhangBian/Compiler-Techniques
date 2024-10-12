package frontend.ast.func;

import frontend.ast.block.Block;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.TokenNode;

public class MainFuncDef extends Node {
    public MainFuncDef() {
        super(SyntaxType.MAIN_FUNC_DEF);
    }

    @Override
    public void Parse() {
        // 只需要识别，不需要判断对错
        // int
        this.AddNode(new TokenNode());
        // main
        this.AddNode(new TokenNode());
        // (
        this.AddNode(new TokenNode());
        Read();
        // )
        // TODO：括号不匹配，添加错误
        this.AddNode(new TokenNode());
        // Block
        this.AddNode(new Block());
    }
}
