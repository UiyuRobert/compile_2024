package Middle.LLVMIR.Values.Instructions;

import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRUse;
import Middle.LLVMIR.IRValue;

/**
 * zext..to : <result> = zext <ty> <value> to <ty2>
 * LLVM IR Zext Zero Extend 类型转换，用于进行0拓展，从i1（一般在比较结果中出现）转换到i32需要用到zext
 */
public class IRZext extends IRInstruction {
    private IRType targetType; // 目标类型
    private IRValue toExt;

    public IRZext(IRValue toExt, IRType targetType) {
        super(IRInstrType.Zext, targetType, 1);
        this.targetType = targetType;
        this.toExt = toExt;
        IRUse use = new IRUse(this, toExt, 0);
        this.addUse(use);
        toExt.addUse(use);
    }

    public String getIR() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName()).append(" = zext ");
        sb.append(toExt.getType().toString()).append(" ");
        sb.append(toExt.getName()).append(" to ").append(targetType).append("\n");
        return sb.toString();
    }
}
