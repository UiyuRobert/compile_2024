package Middle.LLVMIR.Values.Instructions.Calculation;

import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRUse;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.Instructions.IRInstrType;
import Middle.LLVMIR.Values.Instructions.IRInstruction;

/**
 * Binary Calculation
 * <result> = <op> <ty> <op1>, <op2>
 * <op>包括 :
 * - Add +
 * - Sub -
 * - Mul *
 * - Sdiv /
 * - Srem %
 * ty : Type Value类型
 * op1, op2 : 操作数
 */
public class IRBinaryInstr extends IRInstruction {
    public IRBinaryInstr(IRInstrType instrType, IRType retType, IRValue left, IRValue right) {
        super(instrType, retType, 2);
        IRUse use1 = new IRUse(this, left, 0);
        IRUse use2 = new IRUse(this, right, 1);
        this.addUse(use1);
        this.addUse(use2);
        left.addUse(use1);
        right.addUse(use2);
    }

    public String getIR() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName() + " = ");
        switch (this.getInstrType()) {
            case Add:
                sb.append("add nsw ");
                break;
            case Sub:
                sb.append("sub nsw ");
                break;
            case Mul:
                sb.append("mul nsw ");
                break;
            case Sdiv:
                sb.append("sdiv ");
                break;
            case Srem:
                sb.append("srem ");
                break;
            default:
                System.out.println("WTF ! NO SUCH INSTR");
                break;
        }
        sb.append(this.getType()).append(" ");
        sb.append(getOperand(0).getName()).append(", ");
        sb.append(getOperand(1).getName()).append("\n");
        return sb.toString();
    }
}
