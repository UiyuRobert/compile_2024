package Middle.LLVMIR.Values.Instructions.Memory;

import BackEnd.Assembly.AluAsm;
import BackEnd.Assembly.CommentAsm;
import BackEnd.Assembly.MemAsm;
import BackEnd.MipsBuilder;
import BackEnd.Register;
import Middle.LLVMIR.IRTypes.IRArrayType;
import Middle.LLVMIR.IRTypes.IRIntType;
import Middle.LLVMIR.IRTypes.IRPtrType;
import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.Values.Instructions.IRInstrType;
import Middle.LLVMIR.Values.Instructions.IRInstruction;

/**
 * alloca 指令用于在栈上分配内存，并返回一个指向新分配的内存的指针
 * %ptr = alloca <type>
 * 其中，<type>是要分配的内存块的类型。
 * */
public class IRAlloca extends IRInstruction {
    private IRType allocType; // 分配的数据的类型
    private boolean isInit = false;

    public IRAlloca(IRType allocType, String name) {
        super(IRInstrType.Alloca, new IRPtrType(allocType), 0);
        this.allocType = allocType;
        this.setName(name);
    }

    public String getIR() {
        String builder = getName() + " = alloca " +
                allocType.toString() + "\n";
        return builder;
    }

    @Override
    public void toAssembly() {
        new CommentAsm(this.getIR());
        // 分配空间
        MipsBuilder builder = MipsBuilder.builder();
        if (allocType instanceof IRArrayType) {
            builder.allocMemoryInStack(((IRArrayType)allocType).getByteSize());
        } else {
            builder.alloc4BitsInStack();
        }
        // 记录当前栈顶位置到 K0 (此时栈顶也是分配的空间的首地址)
        int offset = builder.getStackOffset();
        new AluAsm(AluAsm.Op.ADDI, Register.K0, Register.SP, offset);
        // 从栈上开一个指针，存放 alloc 分配的地址的首地址
        builder.alloc4BitsInStack();
        offset = builder.getStackOffset();
        builder.mapVarToStackOffset(this, offset);
        new MemAsm(MemAsm.Op.SW, Register.K0, Register.SP, offset);
    }
}
