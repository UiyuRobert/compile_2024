package Middle.LLVMIR.Values.Instructions;

import Middle.LLVMIR.IRTypes.IRIntType;
import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRTypes.IRVoidType;
import Middle.LLVMIR.IRUse;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.IRFunction;
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

    private IRValue speArg = null;

    public IRCall(IRFunction function, ArrayList<IRValue> arguments) {
        // 函数名是第0个参数
        super(IRInstrType.Call, function.getReturnType(), arguments.size() + 1);
        this.arguments = arguments;
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
    }

    /**
     * putint(),putchar(),putstr() -> void
     * */
    public IRCall(String functionName, IRValue val) {
        super(IRInstrType.Call, IRVoidType.Void(), 1);
        this.functionName = "@" + functionName;
        isVoid = true;
        speArg = val;
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
                            .append(" ").append(first.getName());
            }
        } else if (speArg != null) {
            // putstr
            if (speArg instanceof IRGetElePtr)
                stringBuilder.append(((IRGetElePtr) speArg).getIRVal());
            else
                // putint, putch
                stringBuilder.append(speArg.getType()).append(" ").append(speArg.getName());
        } else
            System.out.println("FUCK ! CALL WTF ?");
        // getint getchar
        stringBuilder.append(")\n");
        return stringBuilder.toString();
    }
}
