package Middle.LLVMIR.Values;

import Middle.LLVMIR.IRTypes.IRLabelType;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.Instructions.IRInstruction;
import Middle.LLVMIR.Values.Instructions.IRLabel;

import java.util.ArrayList;

public class IRBasicBlock extends IRValue {
    private ArrayList<IRInstruction> instructions;

    public IRBasicBlock(String name) {
        super(IRLabelType.getLabel(), name);
        this.instructions = new ArrayList<>();
    }

    public void addInstruction(IRInstruction instruction){
        this.instructions.add(instruction);
    }

    public String getIR(String indent) {
        StringBuilder stringBuilder = new StringBuilder();
        for (IRInstruction instruction : instructions) {
            if (!(instruction instanceof IRLabel))
                stringBuilder.append(indent).append(instruction.getIR());
            else stringBuilder.append(instruction.getIR());
        }
        return stringBuilder.toString();
    }
}
