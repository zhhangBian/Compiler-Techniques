package frontend.ast;

import java.util.ArrayList;

public class MainFuncDef extends Node {
    public MainFuncDef() {
        super(SyntaxType.MAIN_FUNC_DEF, new ArrayList<>());
    }

    @Override
    public void Parse() {

    }
}
