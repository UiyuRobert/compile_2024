package Middle.LLVMIR.Values.Instructions.Memory;

import BackEnd.Assembly.CommentAsm;
import BackEnd.Assembly.LaAsm;
import BackEnd.Assembly.MemAsm;
import BackEnd.MipsBuilder;
import BackEnd.Register;
import Middle.LLVMIR.IRTypes.IRIntType;
import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRUse;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.IRGlobalVariable;
import Middle.LLVMIR.Values.Instructions.IRInstrType;
import Middle.LLVMIR.Values.Instructions.IRInstruction;

/**
 * load指令用于从内存中读取数据，并将其加载到寄存器中。load指令的使用格式如下：
 * <result> = load <ty>, ptr <pointer>
 * 其中，<type>是要读取的数据的类型，<ptr>是指向要读取数据的内存块的指针
 * */
public class IRLoad extends IRInstruction {
    private IRValue ptr;

    public IRLoad(IRType irType, IRValue ptr) {
        super(IRInstrType.Load, irType, 1);
        this.ptr = ptr;
        IRUse use = new IRUse(this, ptr, 0);
        this.addUse(use);
        ptr.addUse(use);
    }

    public String getIR() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getName()).append(" = load ");
        builder.append(getType()).append(", ");
        builder.append(ptr.getType()).append(" ");
        builder.append(ptr.getName()).append("\n");
        return builder.toString();
    }

    @Override
    public void toAssembly() {
        new CommentAsm(this.getIR());
        MipsBuilder builder = MipsBuilder.builder();
        Register pointerReg = Register.K0;
        Register resultReg = Register.K0;

        // 求地址
        if (ptr instanceof IRGlobalVariable) {
            // 是全局变量
            new LaAsm(pointerReg, ((IRGlobalVariable) ptr).getMipsName());
        } else {
            /* TODO */ // 函数参数
            int offset = builder.getVarOffsetInStack(ptr);
            new MemAsm(MemAsm.Op.LW, pointerReg, Register.SP, offset);
        }
        // 从栈中取值 load
        boolean isChar = this.getType() == IRIntType.I8();

        if (isChar) new MemAsm(MemAsm.Op.LB, resultReg, pointerReg, 0);
        else new MemAsm(MemAsm.Op.LW, resultReg, pointerReg, 0);

        // 将值存入栈中
        if (isChar) builder.allocCharInStack();
        else builder.alloc4BitsInStack();

        int offset = builder.getStackOffset();
        builder.mapVarToStackOffset(this, offset);

        if (isChar) new MemAsm(MemAsm.Op.SB, resultReg, Register.SP, offset);
        else new MemAsm(MemAsm.Op.SW, resultReg, Register.SP, offset);
    }
}
