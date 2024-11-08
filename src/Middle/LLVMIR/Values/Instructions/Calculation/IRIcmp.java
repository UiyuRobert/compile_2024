package Middle.LLVMIR.Values.Instructions.Calculation;

import Middle.LLVMIR.IRTypes.IRIntType;
import Middle.LLVMIR.IRUse;
import Middle.LLVMIR.IRValue;
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
        sb.append(getCondType()).append(" i32");
        sb.append(operand1.getName()).append(" ").append(operand2.getName()).append("\n");
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
}
