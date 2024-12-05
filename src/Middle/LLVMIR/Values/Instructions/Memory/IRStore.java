package Middle.LLVMIR.Values.Instructions.Memory;

import BackEnd.Assembly.CommentAsm;
import BackEnd.Assembly.LaAsm;
import BackEnd.Assembly.LiAsm;
import BackEnd.Assembly.MemAsm;
import BackEnd.MipsBuilder;
import BackEnd.Register;
import Middle.LLVMIR.IRTypes.IRIntType;
import Middle.LLVMIR.IRTypes.IRPtrType;
import Middle.LLVMIR.IRTypes.IRVoidType;
import Middle.LLVMIR.IRUse;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.IRConstant;
import Middle.LLVMIR.Values.IRGlobalVariable;
import Middle.LLVMIR.Values.Instructions.IRInstrType;
import Middle.LLVMIR.Values.Instructions.IRInstruction;

/**
 * store指令用于将数据从寄存器中写入内存
 * store <type> <val>, <type>* <ptr>
 * <type>是要写入的数据的类型，<val>是要写入的数据的值，<ptr>是指向要写入数据的内存块的指针
 * */
public class IRStore extends IRInstruction {
    private IRValue val2Write;
    private IRValue valOfPtr;

    /**
     * val2Write 要写入的数据的值<br>
     * valOfPtr 指向要写入数据的内存块的指针
     * */
    public IRStore(IRValue val2Write, IRValue valOfPtr) {
        super(IRInstrType.Store, IRVoidType.Void(), 2);
        this.val2Write = val2Write;
        this.valOfPtr = valOfPtr;
        setUseInit();
    }

    private void setUseInit() {
        IRUse use1 = new IRUse(this, val2Write, 0);
        IRUse use2 = new IRUse(this, valOfPtr, 1);
        val2Write.addUse(use1);
        valOfPtr.addUse(use2);
        this.addUse(use1);
        this.addUse(use2);
    }

    public String getIR() {
        StringBuilder builder = new StringBuilder();
        if (val2Write instanceof IRConstant) {
            val2Write.setType(((IRPtrType)valOfPtr.getType()).getPointed());
        }
        builder.append("store ").append(val2Write.getType()).append(" ");
        if (val2Write instanceof IRConstant)
            builder.append(((IRConstant) val2Write).getValue());
        else
            builder.append(val2Write.getName());
        builder.append(", ").append(valOfPtr.getType()).append(" ");
        builder.append(valOfPtr.getName()).append("\n");
        return builder.toString();
    }

    @Override
    public void toAssembly() {
        new CommentAsm(this.getIR());
        MipsBuilder builder = MipsBuilder.builder();
        Register pointerReg = Register.K0;
        Register writeReg = Register.K1;

        // 获取地址
        if (valOfPtr instanceof IRGlobalVariable) {
            new LaAsm(pointerReg, ((IRGlobalVariable) valOfPtr).getMipsName());
        } else {
            /* TODO */
            int offset = builder.getVarOffsetInStack(valOfPtr);
            new MemAsm(MemAsm.Op.LW, pointerReg, Register.SP, offset);
        }
        // 获取要存的值
        if (val2Write instanceof IRConstant) {
            new LiAsm(writeReg, ((IRConstant) val2Write).getValue());
        } else {
            /* TODO */
            int offset = builder.getVarOffsetInStack(val2Write);

            new MemAsm(MemAsm.Op.LW, writeReg, Register.SP, offset);
        }

        // 存值
        new MemAsm(MemAsm.Op.SW, writeReg, pointerReg, 0);

    }
}
