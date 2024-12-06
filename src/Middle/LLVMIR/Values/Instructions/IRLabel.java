package Middle.LLVMIR.Values.Instructions;

import BackEnd.Assembly.LabelAsm;
import Middle.LLVMIR.IRTypes.IRLabelType;
import Middle.LLVMIR.Values.IRBasicBlock;

public class IRLabel extends IRInstruction {
    private static int count = 0;
    private IRBasicBlock belongsTo;

    // opt
    private int jumpNumber;

    public IRLabel() {
        super(IRInstrType.Label, IRLabelType.getLabel(), 0);
        this.setName("%Label" + ++count);
        optimizeInit();
    }

    public void optimizeInit() {
        jumpNumber = 0;
    }

    public boolean isOnlyOneJump() {
        return jumpNumber == 1;
    }

    public IRBasicBlock getBelongsTo() { return belongsTo; }

    public void setBelongsTo(IRBasicBlock belongsTo) { this.belongsTo = belongsTo; }

    public String getIR() {
        return "\n" + getName().substring(1) + ":\n";
    }

    public void setEntry(String funcName) {
        this.setName("%entry_" + funcName);
    }

    public String getMipsName() {
        return getName().substring(1);
    }

    public void addJumpNumber() { ++jumpNumber; }

    @Override
    public void toAssembly() {
        new LabelAsm(getMipsName());
    }
}
