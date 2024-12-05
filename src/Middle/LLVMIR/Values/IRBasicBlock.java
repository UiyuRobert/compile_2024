package Middle.LLVMIR.Values;

import Middle.LLVMIR.IRTypes.IRLabelType;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.Instructions.IRInstruction;
import Middle.LLVMIR.Values.Instructions.IRLabel;

import java.util.ArrayList;

public class IRBasicBlock extends IRValue {
    private static int BLOCK_COUNTER = 0;

    private ArrayList<IRInstruction> instructions;

    // CFG 前驱信息
    private ArrayList<IRBasicBlock> preBlocks;
    // CFG 后继信息
    private ArrayList<IRBasicBlock> sucBlocks;

    // 构建支配树需要的信息
    public int dfsOrder;  // 节点在DFS遍历中的顺序编号
    public int parent; // 节点在DFS树中的父节点编号
    public int semi; // 半支配者编号
    public IRBasicBlock ancestor; // 并查集中的祖先节点
    public IRBasicBlock label; // 节点的标签，用于路径压缩
    public ArrayList<IRBasicBlock> bucket = new ArrayList<>(); // 存储可能的支配者节点
    public IRBasicBlock idom; // 立即支配者节点

    public IRBasicBlock(String name) {
        super(IRLabelType.getLabel(), name);
        this.instructions = new ArrayList<>();
        this.preBlocks = null;
        this.sucBlocks = null;
    }

    public static String getBlockName() {
        return "block_" + BLOCK_COUNTER++;
    }

    public void setPreBlocks(ArrayList<IRBasicBlock> preBlocks) { this.preBlocks = preBlocks; }

    public void setSucBlocks(ArrayList<IRBasicBlock> sucBlocks) { this.sucBlocks = sucBlocks; }

    public ArrayList<IRBasicBlock> getPreBlocks() { return preBlocks; }

    public ArrayList<IRBasicBlock> getSucBlocks() { return sucBlocks; }

    public void addInstruction(IRInstruction instruction){
        this.instructions.add(instruction);
    }

    public IRInstruction getLastInstruction(){
        return this.instructions.get(this.instructions.size()-1);
    }

    public String getIR(String indent) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("; ************************").append(getName()).
                append(" start ****************************\n");
        for (IRInstruction instruction : instructions) {
            if (!(instruction instanceof IRLabel))
                stringBuilder.append(indent).append(instruction.getIR());
            else stringBuilder.append(instruction.getIR());
        }
        stringBuilder.append("; ***************************").append(getName()).
                append(" end *************************\n");
        return stringBuilder.toString();
    }

    @Override
    public void toAssembly() {
        for (IRInstruction instruction : instructions) {
            instruction.toAssembly();
        }
    }
}
