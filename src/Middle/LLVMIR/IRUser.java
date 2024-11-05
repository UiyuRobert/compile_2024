package Middle.LLVMIR;

import Middle.LLVMIR.IRTypes.IRType;

public class IRUser extends IRValue {
    private int operandCnt; // 操作数数量

    public IRUser(IRType type, int operandCnt) {
        super(type);
        this.operandCnt = operandCnt;
    }

    public IRValue getOperand(int i) {
        /* TODO */
        return null;
    }
}
