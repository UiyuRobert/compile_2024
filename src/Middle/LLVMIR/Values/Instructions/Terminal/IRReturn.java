package Middle.LLVMIR.Values.Instructions.Terminal;

import Middle.LLVMIR.IRTypes.IRVoidType;
import Middle.LLVMIR.IRUse;
import Middle.LLVMIR.IRUser;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.Instructions.IRInstrType;
import Middle.LLVMIR.Values.Instructions.IRInstruction;

public class IRReturn extends IRInstruction {
    private boolean isVoid;

    public IRReturn() {
        super(IRInstrType.Ret, IRVoidType.getVoid(), 0);
        this.isVoid = true;
    }

    public IRReturn(IRValue retVal) {
        super(IRInstrType.Ret, retVal.getType(), 1);
        this.isVoid = false;
        IRUse use = new IRUse(this, retVal, 0);
        this.addUse(use);
        retVal.addUse(use);
    }

    public String getIR() {
        if (isVoid)
            return "ret void\n";
        else
            return "ret ";
    }
}
