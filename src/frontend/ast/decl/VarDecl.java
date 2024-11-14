package frontend.ast.decl;

import frontend.ast.token.BType;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;

import java.util.ArrayList;

public class VarDecl extends Node {
    public VarDecl() {
        super(SyntaxType.VAR_DECL);
    }

    @Override
    public void Parse() {
        // BType
        this.AddNode(new BType());
        // VarDef
        this.AddNode(new VarDef());

        // 重复的constDef
        while (GetCurrentTokenType().equals(TokenType.COMMA)) {
            // ,
            this.AddNode(new TokenNode());
            // ConstDef
            this.AddNode(new VarDef());
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
        BType type = (BType) this.components.get(0);
        String typeString = type.GetTokenString();

        for (Node component : this.components) {
            if (component instanceof VarDef varDef) {
                varDef.SetTypeString(typeString);
            }
            component.Visit();
        }
    }

    public ArrayList<VarDef> GetVAeDefs() {
        ArrayList<VarDef> varDefs = new ArrayList<>();
        for (Node node : this.components) {
            if (node instanceof VarDef varDef) {
                varDefs.add(varDef);
            }
        }
        return varDefs;
    }
}
