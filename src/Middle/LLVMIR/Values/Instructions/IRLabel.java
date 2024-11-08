package Middle.LLVMIR.Values.Instructions;

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
}
