package Middle.LLVMIR.Values.Instructions.Memory;

import Middle.LLVMIR.IRTypes.IRPtrType;
import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.Instructions.IRInstrType;
import Middle.LLVMIR.Values.Instructions.IRInstruction;

/**
 * alloca 指令用于在栈上分配内存，并返回一个指向新分配的内存的指针
 * %ptr = alloca <type>
 * 其中，<type>是要分配的内存块的类型。
 * */
public class IRAlloca extends IRInstruction {
    private IRType allocType; // 分配的数据的类型
    private IRValue operand; // 操作的数据
    private boolean isInit = false;

    public IRAlloca(IRType allocType, IRValue operand) {
        super(IRInstrType.Alloca, new IRPtrType(allocType), 0);
        this.allocType = allocType;
        this.operand = operand;
    }

    public String getIR() {
        StringBuilder builder = new StringBuilder();
        builder.append(operand.getName()).append(" = alloca ");
        builder.append(allocType.toString()).append("\n");
        return builder.toString();
    }
}
