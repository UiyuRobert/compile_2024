package Middle.LLVMIR.Values.Instructions.TypeCasting;

import BackEnd.Assembly.CommentAsm;
import BackEnd.Assembly.MemAsm;
import BackEnd.MipsBuilder;
import BackEnd.Register;
import Middle.LLVMIR.IRTypes.IRIntType;
import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRUse;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.Instructions.IRInstrType;
import Middle.LLVMIR.Values.Instructions.IRInstruction;

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

    public void toAssembly() {
        // 只有转 I32，不存在转 I8
        new CommentAsm(this.getIR());
        MipsBuilder builder = MipsBuilder.builder();
        Register valReg = Register.K0;

        /*TODO*/ // 目前只考虑要转换的值在栈中
        if (toExt.getType() == IRIntType.I1()) {
            new CommentAsm("I1 都用 4Byte 来存，不需要转换\n");
            // 把 Zext 后的结果也映射到 toExt 上
            builder.mapVarToStackOffset(this, builder.getVarOffsetInStack(toExt));
        } else if (toExt.getType() == IRIntType.I8()) {
            // 加载旧值
            int offset = builder.getVarOffsetInStack(toExt);
            new MemAsm(MemAsm.Op.LBU, valReg, Register.SP, offset);
            // 开辟新值
            builder.alloc4BitsInStack();
            offset = builder.getStackOffset();
            builder.mapVarToStackOffset(this, offset);
            new MemAsm(MemAsm.Op.SW, valReg, Register.SP, offset);
        }
    }
}
