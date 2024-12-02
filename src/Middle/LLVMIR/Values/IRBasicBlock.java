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

    public IRInstruction getInstruction(int index) {
        if (index == -1)
            return instructions.get(instructions.size()-1);
        return instructions.get(index);
    }

    public String getIR(String indent) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("; ************************block start ****************************\n");
        for (IRInstruction instruction : instructions) {
            if (!(instruction instanceof IRLabel))
                stringBuilder.append(indent).append(instruction.getIR());
            else stringBuilder.append(instruction.getIR());
        }
        stringBuilder.append("; *************************** block end *************************\n");
        return stringBuilder.toString();
    }

    @Override
    public void toAssembly() {
        for (IRInstruction instruction : instructions) {
            instruction.toAssembly();
        }
    }
}
