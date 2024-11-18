package midend.llvm.instr;

import midend.llvm.IrBuilder;
import midend.llvm.value.IrFunction;
import midend.llvm.value.IrValue;

import java.util.ArrayList;

public class CallInstr extends Instr {
    public CallInstr(IrFunction targetFunction, ArrayList<IrValue> paramList) {
        super(targetFunction.GetReturnType(), InstrType.CALL,
            targetFunction.GetReturnType().IsVoidType() ? "call" : IrBuilder.GetLocalVarName());
        this.AddUseValue(targetFunction);
        paramList.forEach(this::AddUseValue);
    }

    public IrFunction GetTargetFunction() {
        return (IrFunction) this.useValueList.get(0);
    }

    public ArrayList<IrValue> GetParamList() {
        return new ArrayList<>(this.useValueList.subList(1, this.useValueList.size()));
    }

    private boolean IsVoidReturnType() {
        return this.GetTargetFunction().GetReturnType().IsVoidType();
    }

    @Override
    public String toString() {
        final IrFunction targetFunction = this.GetTargetFunction();
        ArrayList<String> paramInfo = new ArrayList<>();
        this.GetParamList().stream()
            .map(param -> param.GetIrType() + " " + param.GetIrName())
            .forEach(paramInfo::add);

        StringBuilder builder = new StringBuilder();
        // 如果不是void
        if (!this.IsVoidReturnType()) {
            builder.append(this.irName + " = ");
        }

        builder.append("call ");
        builder.append(this.IsVoidReturnType() ? "void" : targetFunction.GetReturnType());
        builder.append(" ");
        builder.append(targetFunction.GetIrName());
        // 填充参数
        builder.append("(");
        builder.append(String.join(", ", paramInfo));
        builder.append(")");

        return builder.toString();
    }
}
