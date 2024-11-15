package midend.llvm.instr;

import frontend.ast.exp.Exp;
import midend.llvm.type.IrFunctionType;
import midend.llvm.value.IrFunction;
import midend.llvm.value.IrValue;
import midend.visit.VisitorExp;

import java.util.ArrayList;

public class CallInstr extends Instr {
    public CallInstr(String irName, IrFunction targetFuction, ArrayList<Exp> paramList) {
        super(IrFunctionType.FUNCTION_TYPE, irName, InstrType.CALL);
        this.AddUseValue(targetFuction);
        paramList.stream().map(VisitorExp::VisitExp).forEach(this::AddUseValue);
    }

    public IrFunction GetTargetFunction() {
        return (IrFunction) this.useValueList.get(0);
    }

    public ArrayList<IrValue> GetParamList() {
        return (ArrayList<IrValue>) this.useValueList.subList(1, this.useValueList.size());
    }

    @Override
    public String toString() {
        IrFunction targetFunction = this.GetTargetFunction();
        ArrayList<String> paramInfo = new ArrayList<>();
        this.GetParamList().stream()
            .map(param -> param.GetIrType() + " " + param.GetIrName())
            .forEach(paramInfo::add);

        return (this.irType.IsVoidType() ? "call void " : "call i32 ") +
            targetFunction.GetIrName() +
            "(" +
            String.join(", ", paramInfo) +
            ")";
    }
}
