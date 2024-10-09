package frontend.ast;

import java.util.ArrayList;

public class FuncDef extends Node {
    public FuncDef() {
        super(SyntaxType.FUNC_DEF, new ArrayList<>());
    }

    @Override
    public void Parse() {

    }
}
