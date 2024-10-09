package frontend.ast.decl;

import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.TokenNode;

import java.util.ArrayList;

public class ConstDecl extends Node {
    public ConstDecl() {
        super(SyntaxType.CONST_DECL, new ArrayList<>());
    }

    @Override
    public void Parse() {
        // 得到const
        Node node = new TokenNode(GetCurrentToken());
        Read();
    }
}
