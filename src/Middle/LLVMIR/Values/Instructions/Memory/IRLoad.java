package Middle.LLVMIR.Values.Instructions.Memory;

import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRUse;
import Middle.LLVMIR.IRValue;
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
}
