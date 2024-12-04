package Middle.LLVMIR.Values.Instructions;

import BackEnd.Assembly.LabelAsm;
import Middle.LLVMIR.IRTypes.IRLabelType;

public class IRLabel extends IRInstruction {
    private static int count = 0;

    public IRLabel() {
        super(IRInstrType.Label, IRLabelType.getLabel(), 0);
        this.setName("%Label" + ++count);
    }

    public String getIR() {
        return "\n" + getName().substring(1) + ":\n";
    }

    public void setEntry(String funcName) {
        this.setName("%entry_" + funcName);
    }

    public String getMipsName() {
        return getName().substring(1);
    }

    @Override
    public void toAssembly() {
        new LabelAsm(getMipsName());
    }
}
