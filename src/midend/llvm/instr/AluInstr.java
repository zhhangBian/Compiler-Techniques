package midend.llvm.instr;

import backend.mips.Register;
import backend.mips.assembly.MipsAlu;
import backend.mips.assembly.MipsCompare;
import backend.mips.assembly.MipsMdu;
import backend.mips.assembly.fake.MarsLi;
import midend.llvm.constant.IrConstant;
import midend.llvm.type.IrBaseType;
import midend.llvm.value.IrValue;
import utils.Setting;

import java.math.BigInteger;

public class AluInstr extends Instr {
    public enum AluType {
        ADD,
        SUB,
        AND,
        OR,
        MUL,
        SDIV,
        SREM;

        public boolean CanChangeOrder() {
            return switch (this) {
                case ADD, AND, OR, MUL -> true;
                default -> false;
            };
        }
    }

    private final AluType aluOp;

    public AluInstr(String aluOp, IrValue valueL, IrValue valueR) {
        super(IrBaseType.INT32, InstrType.ALU);
        this.aluOp = this.GenerateAluOp(aluOp);
        this.AddUseValue(valueL);
        this.AddUseValue(valueR);
    }

    public AluType GetAluOp() {
        return this.aluOp;
    }

    @Override
    public boolean DefValue() {
        return true;
    }

    @Override
    public String GetGvnHash() {
        String valueL = this.GetValueL().GetIrName();
        String valueR = this.GetValueR().GetIrName();
        // 按照字典序
        return (this.aluOp.CanChangeOrder() && valueL.compareTo(valueR) >= 0) ?
            valueL + " " + this.aluOp + " " + valueR :
            valueR + " " + this.aluOp + " " + valueL;
    }

    @Override
    public String toString() {
        IrValue irValueL = this.GetValueL();
        IrValue irValueR = this.GetValueR();

        return this.irName + " = " + this.aluOp.toString().toLowerCase() +
            " i32 " + irValueL.GetIrName() + ", " + irValueR.GetIrName();
    }

    @Override
    public void toMips() {
        super.toMips();

        IrValue valueL = this.GetValueL();
        IrValue valueR = this.GetValueR();

        Register registerL = this.GetRegisterOrK0ForValue(valueL);
        Register registerR = this.GetRegisterOrK1ForValue(valueR);
        // 为计算结果分配寄存器
        Register registerResult = this.GetRegisterOrK0ForValue(this);

        if (Setting.FINE_TUNING) {
            // 如果是乘除指令
            if (this.IsMduInstr()) {
                switch (this.aluOp) {
                    case MUL ->
                        this.MulOptimize(valueL, valueR, registerL, registerR, registerResult);
                    case SDIV ->
                        this.DivOptimize(valueL, valueR, registerL, registerR, registerResult);
                    case SREM ->
                        this.RemOptimize(valueL, valueR, registerL, registerR, registerResult);
                    default -> {
                    }
                }
            }
            // 如果不是
            else {
                if (valueR instanceof IrConstant irConstant) {
                    this.LoadValueToRegister(valueL, registerL);
                    this.GenerateAluMipsInstr(registerL, irConstant, registerResult);
                } else {
                    LoadValueToRegister(valueL, registerL);
                    LoadValueToRegister(valueR, registerR);
                    // 生成计算指令
                    this.GenerateAluMipsInstr(registerL, registerR, registerResult);
                }
            }
        }
        // 不优化的质朴翻译
        else {
            LoadValueToRegister(valueL, registerL);
            LoadValueToRegister(valueR, registerR);
            // 生成计算指令
            this.GenerateAluMipsInstr(registerL, registerR, registerResult);
        }

        // 如果没有寄存器保留结果，则应该把结果存到栈上
        this.SaveRegisterResult(this, registerResult);
    }

    public IrValue GetValueL() {
        return this.useValueList.get(0);
    }

    public IrValue GetValueR() {
        return this.useValueList.get(1);
    }

    private AluType GenerateAluOp(String aluOp) {
        return switch (aluOp) {
            case "+" -> AluType.ADD;
            case "-" -> AluType.SUB;
            case "*" -> AluType.MUL;
            case "/" -> AluType.SDIV;
            case "%" -> AluType.SREM;
            case "&", "&&" -> AluType.AND;
            case "|", "||" -> AluType.OR;
            default -> throw new RuntimeException("illegal alu op");
        };
    }

    private void GenerateAluMipsInstr(Register registerL, Register registerR,
                                      Register registerResult) {
        switch (this.aluOp) {
            case ADD -> new MipsAlu(MipsAlu.AluType.ADDU, registerResult, registerL, registerR);
            case SUB -> new MipsAlu(MipsAlu.AluType.SUBU, registerResult, registerL, registerR);
            case AND -> new MipsAlu(MipsAlu.AluType.AND, registerResult, registerL, registerR);
            case OR -> new MipsAlu(MipsAlu.AluType.OR, registerResult, registerL, registerR);
            case MUL -> {
                new MipsMdu(MipsMdu.MduType.MULT, registerL, registerR);
                new MipsMdu(MipsMdu.MduType.MFLO, registerResult);
            }
            case SDIV -> {
                new MipsMdu(MipsMdu.MduType.DIV, registerL, registerR);
                new MipsMdu(MipsMdu.MduType.MFLO, registerResult);
            }
            case SREM -> {
                new MipsMdu(MipsMdu.MduType.DIV, registerL, registerR);
                new MipsMdu(MipsMdu.MduType.MFHI, registerResult);
            }
            default -> throw new RuntimeException("illegal alu type");
        }
    }

    private void GenerateAluMipsInstr(Register registerL, IrConstant irConstant,
                                      Register registerResult) {
        int immediate = Integer.parseInt(irConstant.GetIrName());
        switch (this.aluOp) {
            case ADD -> new MipsAlu(MipsAlu.AluType.ADDIU, registerResult, registerL, immediate);
            case SUB -> new MipsAlu(MipsAlu.AluType.ADDIU, registerResult, registerL, -immediate);
            case AND -> new MipsAlu(MipsAlu.AluType.ANDI, registerResult, registerL, immediate);
            case OR -> new MipsAlu(MipsAlu.AluType.ORI, registerResult, registerL, immediate);
            default -> throw new RuntimeException("illegal alu type");
        }
    }

    private boolean IsMduInstr() {
        return this.aluOp.equals(AluType.MUL) ||
            this.aluOp.equals(AluType.SDIV) ||
            this.aluOp.equals(AluType.SREM);
    }

    // 1.若两个操作数都是常数，直接计算结果
    // 2.若一个操作数是常数，另一个是变量，判断常数是否是2的倍数，若是，生成sll指令
    // 3.其他情况，生成mul指令
    private void MulOptimize(IrValue valueL, IrValue valueR,
                             Register registerL, Register registerR,
                             Register registerResult) {
        boolean canOptimize = false;
        // 均为常数
        if (valueL instanceof IrConstant && valueR instanceof IrConstant) {
            int numL = Integer.parseInt(valueL.GetIrName());
            int numR = Integer.parseInt(valueR.GetIrName());
            int numResult = numL * numR;
            new MarsLi(registerResult, numResult);
            canOptimize = true;
        }
        // 左值为常数
        else if (valueL instanceof IrConstant irConstantL) {
            canOptimize = this.MulSingleConstant(valueR, irConstantL, registerR, registerResult);
        }
        // 右值为常数
        else if (valueR instanceof IrConstant irConstantR) {
            canOptimize = this.MulSingleConstant(valueL, irConstantR, registerL, registerResult);
        }

        if (!canOptimize) {
            this.LoadValueToRegister(valueL, registerL);
            this.LoadValueToRegister(valueR, registerR);
            new MipsMdu(MipsMdu.MduType.MULT, registerL, registerR);
            new MipsMdu(MipsMdu.MduType.MFLO, registerResult);
        }
    }

    private boolean MulSingleConstant(IrValue value, IrConstant irConstant,
                                      Register registerValue, Register registerResult) {
        int num = Integer.parseInt(irConstant.GetIrName());
        if (num == 0) {
            new MarsLi(registerResult, 0);
            return true;
        } else if (num == 1) {
            this.LoadValueToRegister(value, registerResult);
            return true;
        } else if (num == -1) {
            this.LoadValueToRegister(value, registerResult);
            new MipsAlu(MipsAlu.AluType.SUBU, registerResult, Register.ZERO, registerResult);
            return true;
        } else {
            num = this.GetTwoShiftNum(num);
            if (num != -1) {
                this.LoadValueToRegister(value, registerValue);
                new MipsAlu(MipsAlu.AluType.SLL, registerResult, registerValue, num);
                return true;
            }
        }
        return false;
    }

    private void DivOptimize(IrValue valueL, IrValue valueR,
                             Register registerL, Register registerR,
                             Register registerResult) {
        // 均为常数
        if (valueL instanceof IrConstant && valueR instanceof IrConstant) {
            int numL = Integer.parseInt(valueL.GetIrName());
            int numR = Integer.parseInt(valueR.GetIrName());
            new MarsLi(registerResult, numL / numR);
        }
        // 右值为常数
        else if (valueR instanceof IrConstant) {
            int num = Integer.parseInt(valueR.GetIrName());
            if (num == 1) {
                this.LoadValueToRegister(valueL, registerResult);
            } else if (num == -1) {
                this.LoadValueToRegister(valueL, registerL);
                new MipsAlu(MipsAlu.AluType.SUBU, registerResult, Register.ZERO, registerL);
            }
            // 一般情况：转化为除以无符号常数的除法优化
            else {
                this.DivSingleConstant(valueL, num, registerL, registerResult);
            }
        }
        // 一般情况
        else {
            this.LoadValueToRegister(valueL, registerL);
            this.LoadValueToRegister(valueR, registerR);
            new MipsMdu(MipsMdu.MduType.DIV, registerL, registerR);
            new MipsMdu(MipsMdu.MduType.MFLO, registerResult);
        }
    }

    private void RemOptimize(IrValue valueL, IrValue valueR,
                             Register registerL, Register registerR,
                             Register registerResult) {
        // 均为常数
        if (valueL instanceof IrConstant && valueR instanceof IrConstant) {
            int numL = Integer.parseInt(valueL.GetIrName());
            int numR = Integer.parseInt(valueR.GetIrName());
            new MarsLi(registerResult, numL % numR);
        }
        // 右值为常数
        else if (valueR instanceof IrConstant) {
            int num = Integer.parseInt(valueR.GetIrName());
            // 一般情况：先除优化，再减，总归是优化
            this.LoadValueToRegister(valueL, Register.FP);
            // div中会用到K1
            this.DivSingleConstant(valueL, num, registerL, Register.GP);
            // 进行乘
            int shift = this.GetTwoShiftNum(num);
            if (shift != -1) {
                new MipsAlu(MipsAlu.AluType.SLL, Register.GP, Register.GP, shift);
            }
            // 没有乘优化
            else {
                // 需要手动管理寄存器，不然还是会乱
                new MarsLi(Register.K0, num);
                new MipsMdu(MipsMdu.MduType.MULT, Register.GP, Register.K0);
                new MipsMdu(MipsMdu.MduType.MFLO, Register.GP);
            }
            new MipsAlu(MipsAlu.AluType.SUBU, registerResult, Register.FP, Register.GP);
        } else {
            this.LoadValueToRegister(valueL, registerL);
            this.LoadValueToRegister(valueR, registerR);
            new MipsMdu(MipsMdu.MduType.DIV, registerL, registerR);
            new MipsMdu(MipsMdu.MduType.MFHI, registerResult);
        }
    }

    private int GetTwoShiftNum(int num) {
        for (int i = 1; i < 32; i++) {
            if (num == 1 << i) {
                return i;
            }
        }
        return -1;
    }

    // dst <- n / d
    // 这里寄存器分配会乱掉，手动进行一些管理
    private void DivSingleConstant(IrValue value, long divisionValue,
                                   Register valueRegister, Register resultRegister) {
        Multiplier multiplier = this.GetMultiplier(Math.abs(divisionValue), 31);
        BigInteger multiplyValue = multiplier.GetMultiplyValue();
        int post = multiplier.GetPost();

        this.LoadValueToRegister(value, Register.K1);
        if (multiplyValue.compareTo(BigInteger.ONE.shiftLeft(31)) < 0) {
            new MarsLi(resultRegister, multiplyValue.intValue());
            new MipsMdu(MipsMdu.MduType.MULT, resultRegister, Register.K1);
            new MipsMdu(MipsMdu.MduType.MFHI, resultRegister);
        } else {
            multiplyValue = multiplyValue.subtract(BigInteger.ONE.shiftLeft(32));
            multiplier.SetM(multiplyValue);

            new MarsLi(resultRegister, multiplyValue.intValue());
            new MipsMdu(MipsMdu.MduType.MULT, resultRegister, Register.K1);
            new MipsMdu(MipsMdu.MduType.MFHI, resultRegister);
            new MipsAlu(MipsAlu.AluType.ADDU, resultRegister, resultRegister, Register.K1);
        }

        if (post > 0) {
            new MipsAlu(MipsAlu.AluType.SRA, resultRegister, resultRegister, post);
        }

        new MipsCompare(MipsCompare.CompareType.SLT, Register.K1, Register.K1, Register.ZERO);
        new MipsAlu(MipsAlu.AluType.ADDU, resultRegister, resultRegister, Register.K1);

        if (divisionValue < 0) {
            new MipsAlu(MipsAlu.AluType.SUBU, resultRegister, Register.ZERO, resultRegister);
        }
    }

    private class Multiplier {
        private BigInteger multiplyValue;
        private final int leftShift;
        private final int post;

        public Multiplier(BigInteger multiplyValue, int post, int leftShift) {
            this.multiplyValue = multiplyValue;
            this.post = post;
            this.leftShift = leftShift;
        }

        public BigInteger GetMultiplyValue() {
            return this.multiplyValue;
        }

        public int GetLeftShift() {
            return this.leftShift;
        }

        public int GetPost() {
            return this.post;
        }

        public void SetM(BigInteger m) {
            this.multiplyValue = m;
        }
    }

    private Multiplier GetMultiplier(long d, int prec) {
        int l = 32 - CountLestZero(d - 1); // l = log2(d) 上取整
        int post = l;

        BigInteger shiftedValue = BigInteger.ONE.shiftLeft(32 + l);
        BigInteger low = shiftedValue.divide(BigInteger.valueOf(d));
        BigInteger high = shiftedValue.add(BigInteger.ONE.shiftLeft(32 + l - prec))
            .divide(BigInteger.valueOf(d));
        while ((low.shiftRight(1).compareTo(high.shiftRight(1)) < 0) && post > 0) {
            low = low.shiftRight(1);
            high = high.shiftRight(1);
            post--;
        }
        return new Multiplier(high, post, l);
    }

    // x二进制表示从最高位开始（左起）的连续的0的个数
    private int CountLestZero(long x) {
        int count = 0;
        for (int i = 31; i >= 0; i--) {
            if ((x & (1L << i)) != 0) {
                break;
            }
            count++;
        }
        return count;
    }
}
