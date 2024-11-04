package Middle.LLVMIR.Values;

import Middle.LLVMIR.IRTypes.IRLabelType;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.Instructions.IRInstruction;

import java.util.ArrayList;

public class IRBasicBlock extends IRValue {
    private ArrayList<IRInstruction> instructions;

    public IRBasicBlock(String name) {
        super(IRLabelType.getLabel(), name);
        this.instructions = new ArrayList<>();
    }
}
