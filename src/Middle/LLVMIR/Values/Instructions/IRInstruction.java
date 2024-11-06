package Middle.LLVMIR.Values.Instructions;

import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRUser;

public class IRInstruction extends IRUser {
    private IRInstrType instrType; // 指令类型

    /**
     * 此处的 irType 是语句的返回值类型
     * */
    public IRInstruction(IRInstrType instrType, IRType irType, int operandCnt) {
        super(irType, operandCnt);
        this.instrType = instrType;
    }

    public String getIR() {
        return "";
    }
}
