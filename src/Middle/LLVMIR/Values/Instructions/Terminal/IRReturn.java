package Middle.LLVMIR.Values.Instructions.Terminal;

import Middle.LLVMIR.IRTypes.IRVoidType;
import Middle.LLVMIR.IRUse;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.Instructions.IRInstrType;
import Middle.LLVMIR.Values.Instructions.IRInstruction;

/**
 * ret <type> <value>
 * <type>是返回值的类型，<value>是返回的值。如果函数没有返回值，则<type>应该是void
 * */
public class IRReturn extends IRInstruction {
    private boolean isVoid;
    private IRValue retVal;

    public IRReturn() {
        super(IRInstrType.Ret, IRVoidType.Void(), 0);
        this.isVoid = true;
    }

    public IRReturn(IRValue retVal) {
        super(IRInstrType.Ret, retVal.getType(), 1);
        this.isVoid = false;
        this.retVal = retVal;
        IRUse use = new IRUse(this, retVal, 0);
        this.addUse(use);
        retVal.addUse(use);
    }

    public String getIR() {
        if (isVoid)
            return "ret void\n";
        else
            return "ret " + retVal.getType() + " " + retVal.getName() + "\n";
    }
}
