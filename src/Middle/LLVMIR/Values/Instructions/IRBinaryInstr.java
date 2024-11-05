package Middle.LLVMIR.Values.Instructions;

import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRUse;
import Middle.LLVMIR.IRValue;

/**
 * Binary Calculation
 * <result> = <op> <ty> <op1>, <op2>
 * <op>包括 :
 * - Add +
 * - Sub -
 * - Mul *
 * - Div /
 * - Lt < Less Than
 * - Le <= Less or Equal
 * - Ge >= Greater or Equal
 * - Gt > Greater
 * - Eq == Equal
 * - Ne != Not Equal
 * - And &
 * - Or |
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
}
