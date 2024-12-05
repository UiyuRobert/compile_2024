package Middle.LLVMIR.Values;

import BackEnd.Assembly.LabelAsm;
import BackEnd.MipsBuilder;
import Middle.LLVMIR.IRTypes.IRFuncType;
import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRUse;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.Instructions.IRLabel;

import java.util.ArrayList;
import java.util.HashMap;

public class IRFunction extends IRValue {
    private int counter;
    private ArrayList<IRBasicBlock> blocks; // 函数内部基本块集合

    // 前驱 CFG
    private HashMap<IRBasicBlock, ArrayList<IRBasicBlock>> preGraph;
    // 后继 CFG
    private HashMap<IRBasicBlock, ArrayList<IRBasicBlock>> sucGraph;

    public IRFunction(IRType type, String name) {
        super(type, name); //
        counter = 1;
        blocks = new ArrayList<>();
        preGraph = null;
        sucGraph = null;
    }

    public void setPreGraph(HashMap<IRBasicBlock, ArrayList<IRBasicBlock>> preGraph) {
        this.preGraph = preGraph;
    }

    public void setSucGraph(HashMap<IRBasicBlock, ArrayList<IRBasicBlock>> sucGraph) {
        this.sucGraph = sucGraph;
    }

    public IRBasicBlock getEntryBlock() { return blocks.get(0); }

    /* 计算变量的下标 */
    public int getCounter() {
        return counter++;
    }

    public void addBlock(IRBasicBlock block) {
        blocks.add(block);
    }

    public ArrayList<IRBasicBlock> getBlocks() { return blocks; }

    public IRType getReturnType() {
        return ((IRFuncType)getType()).getReturnType();
    }

    public String processParams(ArrayList<IRValue> params) {
        StringBuilder ret = new StringBuilder();
        if (!params.isEmpty()) {
            ret.append(params.get(0).getType()).append(" ").append(params.get(0).getName());
            for (int i = 1; i < params.size(); i++)
                ret.append(", ").append(params.get(i).getType()).append(" ").append(params.get(i).getName());
            return ret.toString();
        }
        return "";
    }

    public String getIR() {
        StringBuilder builder = new StringBuilder();
        builder.append("define dso_local ");
        builder.append(((IRFuncType)getType()).getReturnType()).append(" ");
        builder.append(getName()).append("(");
        builder.append(processParams(((IRFuncType)getType()).getParameters()));
        builder.append(") {\n");
        for (IRBasicBlock block : blocks)
            builder.append(block.getIR("\t"));
        builder.append("}\n");
        return builder.toString();
    }

    public void toAssembly() {
        // 函数的开始 label
        new LabelAsm(this.getName().substring(1));

        MipsBuilder builder = MipsBuilder.builder();
        // 进入新函数清空了 新$SP的offset，但是因为有函数参数的存在，需要设置参数与offset的对应关系
        builder.enterNewFunction(this);
        ArrayList<IRValue> parameters = ((IRFuncType)this.getType()).getParameters();
        for (IRValue param : parameters) {
            builder.alloc4BitsInStack();
            int offset = builder.getStackOffset();
            builder.mapVarToStackOffset(param, offset);
        }

        /*TODO*/
        for (IRBasicBlock block : blocks) {
            block.toAssembly();
        }
    }
}
