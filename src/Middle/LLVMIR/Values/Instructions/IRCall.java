package Middle.LLVMIR.Values.Instructions;

import BackEnd.Assembly.*;
import BackEnd.MipsBuilder;
import BackEnd.Register;
import Middle.LLVMIR.IRTypes.IRIntType;
import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRTypes.IRVoidType;
import Middle.LLVMIR.IRUse;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.IRConstant;
import Middle.LLVMIR.Values.IRFunction;
import Middle.LLVMIR.Values.IRGlobalVariable;
import Middle.LLVMIR.Values.Instructions.Memory.IRGetElePtr;

import java.util.ArrayList;

/**
 * call指令用于调用函数
 * %result = call <type> <function>(<argument list>)
 * <type>是函数返回值的类型，<function>是要调用的函数的名称，<argument list>是函数参数的列表
 * */
public class IRCall extends IRInstruction {
    private boolean isVoid = false;
    private String functionName;
    private ArrayList<IRValue> arguments;

    private IRValue speArg;

    private IRGlobalVariable privateStr;

    public IRCall(IRFunction function, ArrayList<IRValue> arguments) {
        // 函数名是第0个参数
        super(IRInstrType.Call, function.getReturnType(), arguments.size() + 1);
        this.arguments = arguments;
        speArg = null;
        if (function.getReturnType() == IRVoidType.Void())
            this.isVoid = true;
        IRUse use = new IRUse(this, function, 0);
        function.addUse(use);
        this.addUse(use);
        int index = 1;
        for (IRValue argument : arguments) {
            use = new IRUse(this, argument, index++);
            this.addUse(use);
            argument.addUse(use);
        }
        this.functionName = function.getName();
    }

    /**
     * getint(), getchar() -> i32
     * */
    public IRCall(String functionName) {
        super(IRInstrType.Call, IRIntType.I32(), 0);
        this.functionName = "@" + functionName;
        isVoid = false;
        speArg = null;
    }

    /**
     * putint(),putchar(),putstr() -> void
     * */
    public IRCall(String functionName, IRValue val, IRGlobalVariable privateStr) {
        super(IRInstrType.Call, IRVoidType.Void(), 1);
        this.functionName = "@" + functionName;
        isVoid = true;
        speArg = val;
        this.privateStr = privateStr;
        IRUse use = new IRUse(this, val, 0);
        this.addUse(use);
        val.addUse(use);
    }

    public String getIR() {
        StringBuilder stringBuilder = new StringBuilder();
        if (!isVoid)
            stringBuilder.append(getName()).append(" = ");
        stringBuilder.append("call ");
        stringBuilder.append(getType()).append(" ");
        stringBuilder.append(functionName).append("(");
        if (arguments != null && !arguments.isEmpty()) {
            IRValue first = arguments.get(0);
            stringBuilder.append(first.getType()).append(" ").append(first.getName());
            if (arguments.size() > 1) {
                for (int i = 1; i < arguments.size(); i++)
                    stringBuilder.append(", ").append(arguments.get(i).getType())
                            .append(" ").append(arguments.get(i).getName());
            }
        } else if (speArg != null) {
            // putstr
            if (speArg instanceof IRGetElePtr)
                stringBuilder.append(((IRGetElePtr) speArg).getIRVal());
            else
                // putint, putch
                stringBuilder.append(speArg.getType()).append(" ").append(speArg.getName());
        }
        // getint getchar
        stringBuilder.append(")\n");
        return stringBuilder.toString();
    }

    @Override
    public void toAssembly() {
        new CommentAsm(this.getIR());

        if (functionName.equals("@getint") || functionName.equals("@getchar"))
            input2Assembly();
        else if (functionName.equals("@putint") || functionName.equals("@putchar")
                || functionName.equals("@putstr"))
            put2Assembly();
        else
            normalCall2Assembly();
    }

    private void input2Assembly() {
        MipsBuilder builder = MipsBuilder.builder();

        new LiAsm(Register.V0, 5);
        new SyscallAsm();
        /* TODO */ // 给this分配寄存器后
        builder.alloc4BitsInStack();
        int offset = builder.getStackOffset();
        builder.mapVarToStackOffset(this, offset);
        new MemAsm(MemAsm.Op.SW, Register.V0, Register.SP, offset);
    }

    private void put2Assembly() {
        MipsBuilder builder = MipsBuilder.builder();

        if (functionName.equals("@putint")) {
            // 加载服务号
            new LiAsm(Register.V0, 1);
            // 加载数据
            if (speArg instanceof IRConstant) {
                int value = ((IRConstant) speArg).getValue();
                new LiAsm(Register.A0, value);
            } else {
                int offset = builder.getVarOffsetInStack(speArg);
                new MemAsm(MemAsm.Op.LW, Register.A0, Register.SP, offset);
            }
        } else if (functionName.equals("@putchar")) {
            // 加载服务号
            new LiAsm(Register.V0, 11);
            // 加载数据
            if (speArg instanceof IRConstant) {
                int value = ((IRConstant) speArg).getValue();
                new LiAsm(Register.A0, value);
            } else {
                int offset = builder.getVarOffsetInStack(speArg);
                new MemAsm(MemAsm.Op.LB, Register.A0, Register.SP, offset);
            }
        } else {
            // putstr()
            new LiAsm(Register.V0, 4);
            new LaAsm(Register.A0, privateStr.getMipsName());
        }

        // 系统调用
        new SyscallAsm();
    }

    private void normalCall2Assembly() {
        MipsBuilder builder = MipsBuilder.builder();

        // 保存寄存器
        ArrayList<Register> allocated = builder.getAllocatedRegs();
        int regCnt = 0;
        int curOffset = builder.getStackOffset();
        for (Register reg : allocated) {
            ++regCnt;
            new MemAsm(MemAsm.Op.SW, reg, Register.SP, curOffset - regCnt * 4);
        }
        // 当前栈底 $sp + curOffset - regCnt * 4
        // 存储旧函数的 $SP, $RA
        new MemAsm(MemAsm.Op.SW, Register.SP, Register.SP, curOffset - regCnt * 4 - 4);
        new MemAsm(MemAsm.Op.SW, Register.RA, Register.SP, curOffset - regCnt * 4 - 8);

        // 当前栈底 $sp + curOffset - regCnt * 4 - 8
        /*TODO*/ // 压入参数，之后使用寄存器
        Register tmpReg = Register.K0;

        int argCnt = 0;
        for (IRValue arg : arguments) {
            ++argCnt;
            if (arg instanceof IRConstant)
                new LiAsm(tmpReg, ((IRConstant) arg).getValue());
            else { // 在栈中，需要取值
                int offset = builder.getVarOffsetInStack(arg);
                new MemAsm(MemAsm.Op.LW, tmpReg, Register.SP, offset);
            }
            // 参数压栈
            new MemAsm(MemAsm.Op.SW, tmpReg, Register.SP, (curOffset - regCnt * 4 - 8) - argCnt * 4);
        }
        // 当前栈底 $SP + curOffset - regCnt * 4 - 8 - argCnt * 4
        // 设置被调用函数的 $SP <- (old)$SP + curOffset - regCnt * 4 - 8
        new AluAsm(AluAsm.Op.ADDI, Register.SP, Register.SP, curOffset - regCnt * 4 - 8);
        // 调用函数
        new JumpAsm(JumpAsm.Op.JAL, functionName.substring(1));
        // 恢复$SP、$RA；只能先恢复$RA
        // 相当于(old)$SP + curOffset - regCnt * 4 - 8
        new MemAsm(MemAsm.Op.LW, Register.RA, Register.SP, 0);
        // 相当于(old)$SP + curOffset - regCnt * 4 - 4
        new MemAsm(MemAsm.Op.LW, Register.SP, Register.SP, 4);

        // 恢复寄存器
        regCnt = 0;
        for (Register reg : allocated) {
            ++regCnt;
            new MemAsm(MemAsm.Op.LW, reg, Register.SP, curOffset - regCnt * 4);
        }

        // 如果有返回值，那么从$V0 中取出返回值，压入栈中
        if (!isVoid) {
            builder.alloc4BitsInStack();
            curOffset = builder.getStackOffset();
            builder.mapVarToStackOffset(this, curOffset);
            new MemAsm(MemAsm.Op.SW, Register.V0, Register.SP, curOffset);
        }
    }
}
