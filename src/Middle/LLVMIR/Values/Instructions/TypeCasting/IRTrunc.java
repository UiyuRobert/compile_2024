package Middle.LLVMIR.Values.Instructions.TypeCasting;

import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRUse;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.Instructions.IRInstrType;
import Middle.LLVMIR.Values.Instructions.IRInstruction;

/**
 * trunc指令将一个整数或浮点数截断为比原来小的位数，即去掉高位的一些二进制位。
 * %result = trunc <source type> <value> to <destination type>
 * */
public class IRTrunc extends IRInstruction {
    private IRValue toTrunc;

    public IRTrunc(IRValue toTrunc, IRType target) {
        super(IRInstrType.Trunc, target, 1);
        this.toTrunc = toTrunc;
        IRUse use = new IRUse(this, toTrunc, 0);
        this.addUse(use);
        toTrunc.addUse(use);
    }

    public String getIR() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName()).append(" = trunc ");
        sb.append(toTrunc.getType().toString()).append(" ");
        sb.append(toTrunc.getName()).append(" to ");
        sb.append(this.getType().toString()).append("\n");
        return sb.toString();
    }
}
