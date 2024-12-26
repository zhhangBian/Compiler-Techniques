package frontend.ast.exp;

import error.Error;
import error.ErrorRecorder;
import error.ErrorType;
import frontend.ast.Node;
import frontend.ast.SyntaxType;
import frontend.ast.func.FuncRealParamS;
import frontend.ast.token.Ident;
import frontend.ast.token.TokenNode;
import frontend.lexer.TokenType;
import midend.symbol.FuncSymbol;
import midend.symbol.Symbol;
import midend.symbol.SymbolManger;
import midend.symbol.SymbolType;

import java.util.ArrayList;

public class UnaryExp extends ComputeExp {
    public UnaryExp() {
        super(SyntaxType.UNARY_EXP);
    }

    @Override
    public void Parse() {
        // 函数调用
        if (GetCurrentTokenType().equals(TokenType.IDENFR) &&
            Peek(1).GetTokenType().equals(TokenType.LPARENT)) {
            // Ident
            this.AddNode(new Ident());
            // (
            this.AddNode(new TokenNode());
            // 函数参数
            if (GetCurrentTokenType().equals(TokenType.INTCON) ||
                GetCurrentTokenType().equals(TokenType.CHRCON) ||
                GetCurrentTokenType().equals(TokenType.IDENFR) ||
                GetCurrentTokenType().equals(TokenType.PLUS) ||
                GetCurrentTokenType().equals(TokenType.MINU) ||
                GetCurrentTokenType().equals(TokenType.NOT) ||
                GetCurrentTokenType().equals(TokenType.LPARENT)) {
                this.AddNode(new FuncRealParamS());
            }
            // )
            if (GetCurrentTokenType().equals(TokenType.RPARENT)) {
                this.AddNode(new TokenNode());
            } else {
                this.AddMissRParentError();
            }
        }
        // 带符号的表达式（右递归）
        else if (GetCurrentTokenType().equals(TokenType.PLUS) ||
            GetCurrentTokenType().equals(TokenType.MINU) ||
            GetCurrentTokenType().equals(TokenType.NOT)) {
            //  UnaryOp
            this.AddNode(new UnaryOp());
            //  UnaryExp
            this.AddNode(new UnaryExp());
        }
        // 基本表达式
        else {
            this.AddNode(new PrimaryExp());
        }
    }

    @Override
    public void Visit() {
        for (Node component : this.components) {
            component.Visit();

            this.CheckIdentUnDefinedError(component);
        }
    }

    private void CheckIdentUnDefinedError(Node component) {
        if (!(component instanceof Ident ident)) {
            return;
        }

        String identName = ident.GetTokenString();
        int line = ident.GetLine();
        Symbol symbol = SymbolManger.GetSymbol(identName);

        if (symbol == null) {
            ErrorRecorder.AddError(new Error(ErrorType.NAME_UNDEFINED, line));
            return;
        }

        // 如果是函数，检查参数
        if (symbol instanceof FuncSymbol funcSymbol) {
            this.CheckFuncParam(funcSymbol, line);
        }
    }

    private FuncRealParamS GetFuncRealParaS() {
        for (Node component : this.components) {
            if (component instanceof FuncRealParamS funcRealParamS) {
                return funcRealParamS;
            }
        }
        return null;
    }

    private void CheckFuncParam(FuncSymbol funcSymbol, int line) {
        FuncRealParamS funcRealParamS = this.GetFuncRealParaS();
        // 无参数情况
        if (funcRealParamS == null) {
            if (!funcSymbol.GetFormalParamList().isEmpty()) {
                ErrorRecorder.AddError(new Error(ErrorType.FUNC_PARAM_NUM_NOT_MATCH, line));
            }
        }
        // 有参数
        else {
            ArrayList<Exp> realParamList = funcRealParamS.GetRealParamList();
            int realParamCount = realParamList.size();

            ArrayList<Symbol> formalParamList = funcSymbol.GetFormalParamList();
            int formalParamCount = formalParamList.size();

            // 处理类型不匹配的问题
            if (realParamCount != formalParamCount) {
                ErrorRecorder.AddError(new Error(ErrorType.FUNC_PARAM_NUM_NOT_MATCH, line));
                return;
            }

            this.CheckParamTypeFit(realParamList, formalParamList, line);
        }
    }

    private void CheckParamTypeFit(ArrayList<Exp> realParamList,
                                   ArrayList<Symbol> formalParamList, int line) {
        for (int i = 0; i < formalParamList.size(); i++) {
            Symbol formalSymbol = formalParamList.get(i);
            SymbolType formalType = formalSymbol.GetSymbolType();

            String realParamName = realParamList.get(i).GetSimpleName();
            Symbol realSymbol = SymbolManger.GetSymbol(realParamName);

            switch (formalType) {
                case INT_ARRAY, CHAR_ARRAY ->
                    this.CheckArrayParamTypeFit(realSymbol, formalSymbol, line);
                case INT, CHAR -> this.CheckValueParamTypeFit(realSymbol, formalSymbol, line);
                default -> {
                }
            }
        }
    }

    // 数组类型参数检查：必须严格相同
    private void CheckArrayParamTypeFit(Symbol realSymbol, Symbol formalSymbol, int line) {
        if (realSymbol == null) {
            ErrorRecorder.AddError(new Error(ErrorType.FUNC_PARAM_TYPE_NOT_MATCH, line));
            return;
        }

        if (!realSymbol.GetSymbolType().equals(formalSymbol.GetSymbolType())) {
            ErrorRecorder.AddError(new Error(ErrorType.FUNC_PARAM_TYPE_NOT_MATCH, line));
        }
    }

    // 值类型参数检查：不是数组即可
    private void CheckValueParamTypeFit(Symbol realSymbol, Symbol formalSymbol, int line) {
        // 参数可能是常数，这种情况为null
        if (realSymbol != null) {
            SymbolType realType = realSymbol.GetSymbolType();
            if (realType.equals(SymbolType.INT_ARRAY) || realType.equals(SymbolType.CHAR_ARRAY)) {
                ErrorRecorder.AddError(new Error(ErrorType.FUNC_PARAM_TYPE_NOT_MATCH, line));
            }
        }
    }

    @Override
    public void Compute() {
        Node node = this.components.get(0);
        if (node instanceof PrimaryExp primaryExp) {
            primaryExp.Compute();
            this.isConst = primaryExp.GetIfConst();
            this.value = primaryExp.GetValue();
        } else if (node instanceof UnaryOp unaryOp) {
            UnaryExp exp = (UnaryExp) this.components.get(1);
            exp.Compute();
            this.isConst = exp.GetIfConst();
            this.value = this.GetUnaryOpResult(unaryOp.GetSimpleName(), exp.GetValue());
        }
    }

    private int GetUnaryOpResult(String op, int value) {
        return switch (op) {
            case "+" -> value;
            case "-" -> -value;
            case "!" -> value == 0 ? 1 : 0;
            default -> throw new RuntimeException("Invalid UnaryOp");
        };
    }

    public boolean IsPrimaryType() {
        return this.components.get(0) instanceof PrimaryExp;
    }

    public PrimaryExp GetPrimaryExp() {
        return (PrimaryExp) this.components.get(0);
    }

    public boolean IsIdentType() {
        return this.components.get(0) instanceof Ident;
    }

    public Ident GetIdent() {
        return (Ident) this.components.get(0);
    }

    public ArrayList<Exp> GetRealParamList() {
        FuncRealParamS funcRealParamS = null;
        for (Node component : this.components) {
            if (component instanceof FuncRealParamS funcRealParam) {
                funcRealParamS = funcRealParam;
                break;
            }
        }
        return funcRealParamS == null ? new ArrayList<>() : funcRealParamS.GetRealParamList();
    }

    public boolean IsUnaryType() {
        return this.components.get(0) instanceof UnaryOp;
    }

    public UnaryOp GetUnaryOp() {
        return (UnaryOp) this.components.get(0);
    }

    public UnaryExp GetUnaryExp() {
        return (UnaryExp) this.components.get(1);
    }
}
