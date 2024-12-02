package Middle.LLVMIR.Values.Instructions.Calculation;

import BackEnd.Assembly.CmpAsm;
import BackEnd.Assembly.CommentAsm;
import BackEnd.Assembly.LiAsm;
import BackEnd.Assembly.MemAsm;
import BackEnd.MipsBuilder;
import BackEnd.Register;
import Middle.LLVMIR.IRTypes.IRIntType;
import Middle.LLVMIR.IRUse;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.IRConstant;
import Middle.LLVMIR.Values.Instructions.IRInstrType;
import Middle.LLVMIR.Values.Instructions.IRInstruction;

/**
 * <result> = icmp <cond> <ty> <op1>, <op2>
 * <result>：保存比较结果的变量（布尔类型 i1）
 * <cond>：比较条件，决定 icmp 的比较方式（例如等于、大于、小于等）
 * <ty>：操作数类型，必然为 i32
 * <op1> 和 <op2>：要比较的两个整数值
 *
 * 无符号条件（用于无符号整数比较）：
 * - eq：等于 (==)
 * - ne：不等于 (!=)
 * - ugt：无符号大于 (>)
 * - uge：无符号大于等于 (>=)
 * - ult：无符号小于 (<)
 * - ule：无符号小于等于 (<=)
 *
 * 有符号条件（用于有符号整数比较）：
 * - sgt：有符号大于 (>)
 * - sge：有符号大于等于 (>=)
 * - slt：有符号小于 (<)
 * - sle：有符号小于等于 (<=)
 * */
public class IRIcmp extends IRInstruction {
    private IRValue operand1;
    private IRValue operand2;

    public IRIcmp(IRValue operand1, IRValue operand2, IRInstrType instrType) {
        // instrType 为以上所列的
        super(instrType, IRIntType.I1(), 2);
        this.operand1 = operand1;
        this.operand2 = operand2;
        IRUse use1 = new IRUse(this, operand1, 0);
        IRUse use2 = new IRUse(this, operand2, 1);
        this.addUse(use1);
        this.addUse(use2);
        operand1.addUse(use1);
        operand2.addUse(use2);
    }

    public String getIR() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName()).append(" = icmp ");
        sb.append(getCondType()).append(" i32 ");
        sb.append(operand1.getName()).append(", ").append(operand2.getName()).append("\n");
        return sb.toString();
    }

    public String getCondType() {
        switch (this.getInstrType()) {
            case Eq : return "eq";
            case Ne: return "ne";
            case Sgt: return "sgt";
            case Sge: return "sge";
            case Slt: return "slt";
            case Sle: return "sle";
            default:
                System.out.println("WTF ??? NO SUCH ICMP TYPE");
                return null;
        }
    }

    public void toAssembly() {
        new CommentAsm(this.getIR());
        MipsBuilder builder = MipsBuilder.builder();
        Register resultReg = Register.K0;
        Register leftReg = Register.K0;
        Register rightReg = Register.K0;

        // 如果两个都为常数，直接处理
        if (operand1 instanceof IRConstant && operand2 instanceof IRConstant) {
            int ret = constantCmp();
            new LiAsm(resultReg, ret);
        } else {
            // 处理 operand1
            if (operand1 instanceof IRConstant) {
                new LiAsm(leftReg, ((IRConstant) operand1).getValue());
            } else {
                /* TODO */
                int offset = builder.getVarOffsetInStack(operand1);
                new MemAsm(MemAsm.Op.LW, leftReg, Register.SP, offset);
            }

            // 处理 operand2
            if (operand2 instanceof IRConstant) {
                new LiAsm(leftReg, ((IRConstant) operand2).getValue());
            } else {
                /* TODO */
                int offset = builder.getVarOffsetInStack(operand2);
                new MemAsm(MemAsm.Op.LW, rightReg, Register.SP, offset);
            }

            // 比较
            switch (this.getInstrType()) {
                case Eq : new CmpAsm(CmpAsm.Op.SEQ, resultReg, leftReg, rightReg); break;
                case Ne: new CmpAsm(CmpAsm.Op.SNE, resultReg, leftReg, rightReg); break;
                case Sgt: new CmpAsm(CmpAsm.Op.SGT, resultReg, leftReg, rightReg); break;
                case Sge: new CmpAsm(CmpAsm.Op.SGE, resultReg, leftReg, rightReg); break;
                case Slt: new CmpAsm(CmpAsm.Op.SLT, resultReg, leftReg, rightReg); break;
                case Sle: new CmpAsm(CmpAsm.Op.SLE, resultReg, leftReg, rightReg); break;
            }

            // 存值, I1 都用 4byte 存
            builder.alloc4BitsInStack();
            int offset = builder.getStackOffset();
            builder.mapVarToStackOffset(this, offset);
            new MemAsm(MemAsm.Op.SW, resultReg, Register.SP, offset);
        }
    }

    private int constantCmp() {
        if (operand1 instanceof IRConstant && operand2 instanceof IRConstant) {
            int value1 = ((IRConstant) operand1).getValue();
            int value2 = ((IRConstant) operand2).getValue();
            boolean ret = false;
            switch (this.getInstrType()) {
                case Eq : ret = (value1 == value2); break;
                case Ne: ret = (value1 != value2); break;
                case Sgt: ret = (value1 > value2); break;
                case Sge: ret = (value1 >= value2); break;
                case Slt: ret = (value1 < value2); break;
                case Sle: ret = (value1 <= value2); break;
                default:
                    System.out.println("WTF ??? NO SUCH ICMP TYPE");
            }
            return ret ? 1 : 0;
        }
        System.out.println("WTF ??? NOT CONSTANT");
        return -1;
    }
}
