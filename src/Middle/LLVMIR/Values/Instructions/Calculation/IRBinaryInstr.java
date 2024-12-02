package Middle.LLVMIR.Values.Instructions.Calculation;

import BackEnd.Assembly.*;
import BackEnd.MipsBuilder;
import BackEnd.Register;
import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRUse;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.IRConstant;
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

    @Override
    public void toAssembly() {
        // 此处不需要考虑类型，因为在计算中将所有算术运算的数全部转为了 i32
        new CommentAsm(this.getIR());
        MipsBuilder builder = MipsBuilder.builder();

        IRValue left = this.getOperand(0);
        IRValue right = this.getOperand(1);
        Register reg1 =Register.K0;
        Register reg2 =Register.K1;
        Register target = Register.K0;

        if (left instanceof IRConstant) {
            new LiAsm(reg1, ((IRConstant) left).getValue());
        } else { // 从栈中把值取出来
            /*  TODO */ // 为参数时
            int offset = builder.getVarOffsetInStack(left);
            new MemAsm(MemAsm.Op.LW, reg1, Register.SP, offset);
        }

        if (right instanceof IRConstant) {
            new LiAsm(reg2, ((IRConstant) right).getValue());
        } else { // 从栈中把值取出来
            /*  TODO */ // 为参数时
            int offset = builder.getVarOffsetInStack(right);
            new MemAsm(MemAsm.Op.LW, reg2, Register.SP, offset);
        }

        IRInstrType op = this.getInstrType();
        switch (op) {
            case Add:
                new AluAsm(AluAsm.Op.ADDU, target, reg1, reg2);
                break;
            case Sub:
                new AluAsm(AluAsm.Op.SUBU, target, reg1, reg2);
                break;
            case And:
                new AluAsm(AluAsm.Op.AND, target, reg1, reg2);
                break;
            case Or:
                new AluAsm(AluAsm.Op.OR, target, reg1, reg2);
                break;
            case Mul:
                new MulDivAsm(MulDivAsm.Op.MULT, reg1, reg2);
                new HiLoAsm(HiLoAsm.Op.MFLO, target);
                break;
            case Sdiv:
                new MulDivAsm(MulDivAsm.Op.DIV, reg1, reg2);
                new HiLoAsm(HiLoAsm.Op.MFLO, target);
                break;
            case Srem:
                new MulDivAsm(MulDivAsm.Op.DIV, reg1, reg2);
                new HiLoAsm(HiLoAsm.Op.MFHI, target);
                break;
        }

        // 将算出的值存入栈，处理中所有计算值都为 I32
        builder.alloc4BitsInStack();
        int offset = builder.getStackOffset();
        builder.mapVarToStackOffset(this, offset);
        new MemAsm(MemAsm.Op.SW, target, Register.SP, offset);
    }
}
