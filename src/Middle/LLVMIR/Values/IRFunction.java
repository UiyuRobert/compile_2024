package Middle.LLVMIR.Values;

import BackEnd.Assembly.LabelAsm;
import BackEnd.MipsBuilder;
import Middle.LLVMIR.IRTypes.IRFuncType;
import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRUse;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.Instructions.IRLabel;

import java.util.ArrayList;

public class IRFunction extends IRValue {
    private int counter;
    private ArrayList<IRBasicBlock> blocks; // 函数内部基本块集合

    public IRFunction(IRType type, String name) {
        super(type, name); //
        counter = 1;
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

    public void toAssembly() {
        new LabelAsm(this.getName().substring(1));
        MipsBuilder builder = MipsBuilder.builder();
        builder.enterNewFunction(this);

    }
}
