package Middle.LLVMIR.Values;

import Middle.LLVMIR.IRTypes.IRFuncType;
import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRValue;

import java.util.ArrayList;

public class IRFunction extends IRValue {
    private int counter = 0;
    private ArrayList<IRBasicBlock> blocks; // 函数内部基本块集合

    public IRFunction(IRType type, String name) {
        super(type, name); // 此处的 Type 是函数返回值类型
        counter = 0;
        blocks = new ArrayList<>();
    }

    /* 计算变量的下标 */
    public int getCounter() {
        return counter++;
    }

    public void addBlock(IRBasicBlock block) {
        blocks.add(block);
    }

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
}
