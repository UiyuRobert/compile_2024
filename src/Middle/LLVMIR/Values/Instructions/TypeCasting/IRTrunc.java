package Middle.LLVMIR.Values.Instructions.TypeCasting;

import BackEnd.Assembly.*;
import BackEnd.MipsBuilder;
import BackEnd.Register;
import Middle.LLVMIR.IRTypes.IRIntType;
import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRUse;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.IRConstant;
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

    public void toAssembly() {
        new CommentAsm(this.getIR());
        MipsBuilder builder = MipsBuilder.builder();
        Register resultReg = Register.K0;
        Register srcReg = Register.K1;
        /* TODO */ // 目前只考虑要转换的值在栈中
        if (this.getType() == IRIntType.I1()) { // 先处理转换为 I1 的
            // I1 直接存
            if (toTrunc instanceof IRConstant) {
                int value = ((IRConstant) toTrunc).getValue();
                if (value == 0)
                    new LiAsm(resultReg, 0);
                else new LiAsm(resultReg, 1);
                builder.alloc4BitsInStack();
                int offset = builder.getStackOffset();
                builder.mapVarToStackOffset(this, offset);
                new MemAsm(MemAsm.Op.SW, resultReg, Register.SP, offset);
            } else {
                // 先加载出来，再判断是否为 0
                int offset = builder.getVarOffsetInStack(toTrunc);
                new MemAsm(MemAsm.Op.LW, srcReg, Register.SP, offset);
                new CmpAsm(CmpAsm.Op.SNE, resultReg, Register.ZERO, srcReg);
                new MemAsm(MemAsm.Op.SW, resultReg, Register.SP, offset);
                builder.mapVarToStackOffset(this, offset);
            }
        } else if (this.getType() == IRIntType.I8()) {
            if (toTrunc instanceof IRConstant) {
                int value = ((IRConstant) toTrunc).getValue();
                value &= 0xff;
                new LiAsm(resultReg, value);
                builder.alloc4BitsInStack();
                int offset = builder.getStackOffset();
                builder.mapVarToStackOffset(this, offset);
                new MemAsm(MemAsm.Op.SW, resultReg, Register.SP, offset);
            } else {
                int offset = builder.getVarOffsetInStack(toTrunc);
                new MemAsm(MemAsm.Op.LW, srcReg, Register.SP, offset);
                // 截断高位，只保留后8位
                new AluAsm(AluAsm.Op.ANDI, resultReg, srcReg, 0xff);
                new MemAsm(MemAsm.Op.SW, resultReg, Register.SP, offset);
                builder.mapVarToStackOffset(this, offset);
            }
        } else {
            System.out.println("WTF ??? TRUNC");
        }

    }
}
