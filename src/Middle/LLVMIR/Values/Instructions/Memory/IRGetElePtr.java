package Middle.LLVMIR.Values.Instructions.Memory;

import Middle.LLVMIR.IRTypes.IRArrayType;
import Middle.LLVMIR.IRTypes.IRIntType;
import Middle.LLVMIR.IRTypes.IRPtrType;
import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.IRConstant;
import Middle.LLVMIR.Values.Instructions.IRInstrType;
import Middle.LLVMIR.Values.Instructions.IRInstruction;

import java.util.ArrayList;

/**
 *  getelementptr 指令用于计算指定元素的地址，以便访问内存中的数据
 *  <result> = getelementptr inbounds <ty>, <ty>* <ptrval>{, [inrange] <ty> <idx>}*
 *  <ty>：类型标识符，用于指定数据结构的类型
 *  <ty>* <ptrval>：指向数据结构的基础指针
 *  {, [inrange] <ty> <idx>}*：表示可变长度的索引列表，其中每个索引 <idx> 都有一个类型 <ty>
 * */
public class IRGetElePtr extends IRInstruction {
    private IRValue result;
    private IRValue structVal;// 数据结构
    private ArrayList<IRValue> index;

    public IRGetElePtr(IRValue result, IRValue structVal, ArrayList<IRValue> index) {
        super(IRInstrType.GEP, new IRPtrType(getEleTy(structVal)), index.size() + 1);
        this.structVal = structVal;
        this.index = index;
        this.result = result;
    }

    private static IRType getEleTy(IRValue structVal) {
        IRType eleType = ((IRPtrType)structVal.getType()).getPointed();
        if (eleType == IRIntType.getI32())
            return IRIntType.getI32();
        else if (eleType == IRIntType.getI8())
            return IRIntType.getI8();
        else if (eleType instanceof IRArrayType)
            return ((IRArrayType) eleType).getElementType();
        else {
            System.out.println("FUCK ! GEP SHOULD NOT REACH HERE !");
            return null;
        }
    }

    public String getIR() {
        StringBuilder builder = new StringBuilder();
        builder.append(result.getName()).append(" = getelementptr inbounds ");
        builder.append(((IRPtrType)structVal.getType()).getPointed());
        builder.append(", ").append(structVal.getType()).append(" ");
        builder.append(structVal.getName());
        for (IRValue index : index) {
            builder.append(", ").append(index.getType()).append(" ");
            if (index instanceof IRConstant)
                builder.append(((IRConstant) index).getValue());
            else
                builder.append(index.getName());
        }
        builder.append("\n");
        return builder.toString();
    }
}
