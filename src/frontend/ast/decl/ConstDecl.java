package frontend.ast.decl;

import frontend.ast.token.BType;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

import java.util.ArrayList;

public class ConstDecl extends Node {
    public ConstDecl() {
        super(SyntaxType.CONST_DECL);
    }

    @Override
    public void Parse() {
        // 得到const
        this.AddNode(new TokenNode());
        // BType
        this.AddNode(new BType());
        // ConstDef
        this.AddNode(new ConstDef());

        // 重复的constDef
        while (GetCurrentTokenType().equals(TokenType.COMMA)) {
            // ,
            this.AddNode(new TokenNode());
            // ConstDef
            this.AddNode(new ConstDef());
        }

        // ;
        if (GetCurrentTokenType().equals(TokenType.SEMICN)) {
            this.AddNode(new TokenNode());
        } else {
            this.AddMissSemicnError();
        }
    }

    @Override
    public void Visit() {
        // int | char
        BType type = (BType) this.components.get(1);
        String typeString = type.GetTokenString();

        // 可能会有多个ConstDef
        for (Node component : this.components) {
            // 标注类型
            if (component instanceof ConstDef constDef) {
                constDef.SetTypeString(typeString);
            }
            component.Visit();
        }
    }

    public ArrayList<ConstDef> GetConstDef() {
        ArrayList<ConstDef> constDefs = new ArrayList<>();
        for (Node node : this.components) {
            if (node instanceof ConstDef constDef) {
                constDefs.add(constDef);
            }
        }
        return constDefs;
    }
}
