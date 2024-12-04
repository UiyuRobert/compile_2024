package Middle.LLVMIR.Values.Instructions.Memory;

import BackEnd.Assembly.AluAsm;
import BackEnd.Assembly.CommentAsm;
import BackEnd.Assembly.LaAsm;
import BackEnd.Assembly.MemAsm;
import BackEnd.MipsBuilder;
import BackEnd.Register;
import Middle.LLVMIR.IRTypes.IRArrayType;
import Middle.LLVMIR.IRTypes.IRIntType;
import Middle.LLVMIR.IRTypes.IRPtrType;
import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRUse;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.IRConstant;
import Middle.LLVMIR.Values.IRGlobalVariable;
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
    private IRValue structVal;// 数据结构
    private ArrayList<IRValue> index;

    public IRGetElePtr(IRValue structVal, ArrayList<IRValue> index) {
        /* 这里的返回值类型只考虑了拆包一层的 */
        super(IRInstrType.GEP, new IRPtrType(getEleTy(structVal)), index.size() + 1);
        this.structVal = structVal;
        this.index = index;
        IRUse use = new IRUse(this, structVal, 0);
        this.addUse(use);
        structVal.addUse(use);
        for (int i = 0; i < index.size(); i++) {
            use = new IRUse(this, index.get(i), i + 1);
            this.addUse(use);
            index.get(i).addUse(use);
        }
    }

    public static IRType getEleTy(IRValue structVal) {
        IRType eleType = ((IRPtrType)structVal.getType()).getPointed();
        if (eleType == IRIntType.I32())
            return IRIntType.I32();
        else if (eleType == IRIntType.I8())
            return IRIntType.I8();
        else if (eleType instanceof IRArrayType)
            return ((IRArrayType) eleType).getElementType();
        else {
            IRType t = ((IRPtrType)eleType).getPointed();
            if (t == IRIntType.I32())
                return IRIntType.I32();
            else if (t == IRIntType.I8())
                return IRIntType.I8();
            else if (t instanceof IRArrayType)
                return ((IRArrayType) t).getElementType();
            else {
                System.out.println("FUCK ! GEP SHOULD NOT REACH HERE !");
                System.out.println(t);
                return null;
            }

        }
    }

    public String getIR() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName()).append(" = getelementptr inbounds ");
        builder.append(((IRPtrType)structVal.getType()).getPointed());
        builder.append(", ").append(structVal.getType()).append(" ");
        builder.append(structVal.getName());
        for (IRValue index : index) {
            builder.append(", ").append(index.getType()).append(" ");
            builder.append(index.getName());
        }
        builder.append("\n");
        return builder.toString();
    }

    /**
     * 将 GEP 本身当成一个虚拟变量来使用
     * 仅针对 str
     * */
    public String getIRVal() {
        StringBuilder builder = new StringBuilder();
        builder.append("i8* getelementptr inbounds (");
        builder.append(((IRPtrType)structVal.getType()).getPointed());
        builder.append(", ").append(structVal.getType()).append(" ");
        builder.append(structVal.getName());
        for (IRValue index : index) {
            builder.append(", ").append(index.getType()).append(" ");
            builder.append(index.getName());
        }
        builder.append(")");
        return builder.toString();
    }

    @Override
    public void toAssembly() {
        new CommentAsm(this.getIR());
        IRValue curStruct = structVal; //指向数据结构的指针
        Register baseReg = Register.K0;
        Register resultReg = Register.K0;
        Register offsetReg = Register.K1;
        MipsBuilder builder = MipsBuilder.builder();

        // 获取数组基地址
        if (curStruct instanceof IRGlobalVariable) {
            // 如果是全局变量
            new LaAsm(baseReg, ((IRGlobalVariable) curStruct).getMipsName());
        } else {
            /* TODO */ // 如果是函数参数
            // 是局部变量
            int offset = builder.getVarOffsetInStack(curStruct);
            new MemAsm(MemAsm.Op.LW, baseReg, Register.SP, offset);
        }

        IRType curRank = ((IRPtrType)structVal.getType()).getPointed(); // 当前解析到的层
        IRValue curIndex = index.get(0);

        // 获取偏移量
        if (index.size() > 1) { // 是数组
            curIndex = index.get(index.size() - 1); // 有实际影响的 index
            curRank = ((IRArrayType) curRank).getElementType();
        }

        // 如果 index 为 0, 那么 基地址 即为所求, 也就是 baseReg = resultReg
        if (curIndex instanceof IRConstant) {
            // 常数
            if (((IRConstant) curIndex).getValue() != 0)
                new AluAsm(AluAsm.Op.ADDI, resultReg, baseReg,
                    ((IRConstant) curIndex).getValue() * curRank.getByteSize());
        } else {
            // 变量
            /* TODO */ // 可能为函数参数
            int stackOffset = builder.getVarOffsetInStack(curIndex);
            new MemAsm(MemAsm.Op.LW, offsetReg, Register.SP, stackOffset);

            if (curRank == IRIntType.I32()) { // 是 int 则 *4
                new AluAsm(AluAsm.Op.SLL, Register.K1, offsetReg, 2);
                // 基地址 + 偏移量 -> 结果
                new AluAsm(AluAsm.Op.ADDU, resultReg, baseReg, Register.K1);
            } else // 是 char 则直接使用
                new AluAsm(AluAsm.Op.ADDU, resultReg, baseReg, offsetReg);
        }

        // 将结果存到栈里
        builder.alloc4BitsInStack();
        int offset = builder.getStackOffset();
        builder.mapVarToStackOffset(this, offset);
        new MemAsm(MemAsm.Op.SW, resultReg, Register.SP, offset);
    }
}
