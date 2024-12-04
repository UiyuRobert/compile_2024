package BackEnd;

import BackEnd.Assembly.Asm;
import BackEnd.Assembly.GlobalVarAsm;
import BackEnd.Assembly.LabelAsm;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.IRFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MipsBuilder {
    private static MipsBuilder mipsBuilder = new MipsBuilder();
    public static MipsBuilder builder() { return  mipsBuilder; }

    // 指令
    private ArrayList<Asm> dataSegment;
    private ArrayList<Asm> textSegment;

    // 函数
    private int curFunStackOffset; // 当前栈顶
    private IRFunction curFunction; // 当前处理的函数
    private HashMap<IRValue, Integer> var2StackOffset;  // 变量 -> 栈
    private HashMap<IRValue, Register> var2Register; // 全局寄存器

    private MipsBuilder() {
        this.dataSegment = new ArrayList<>();
        this.textSegment = new ArrayList<>();
    }

    public void addAsmInstr(Asm asm) {
        if (asm instanceof GlobalVarAsm)
            this.dataSegment.add(asm);
        else
            this.textSegment.add(asm);
    }

    public void enterNewFunction(IRFunction function) {
        this.curFunStackOffset = 0;
        this.curFunction = function;
        var2StackOffset = new HashMap();
        // var2Register = new HashMap();
    }

    public void allocMemoryInStack(int size) { curFunStackOffset -= size; }

    public ArrayList<Register> getAllocatedRegs() {
        if (var2Register == null) return new ArrayList<>();
        return new ArrayList<>(new HashSet<>(var2Register.values()));
    }

    public boolean useRegister() { return var2Register != null; }

    public void alloc4BitsInStack() { curFunStackOffset -= 4; }

    public int getStackOffset() { return curFunStackOffset; }

    public void mapVarToStackOffset(IRValue var, int offset) { var2StackOffset.put(var, offset); }

    public Integer getVarOffsetInStack(IRValue var) {
        if (var2StackOffset.containsKey(var)) return var2StackOffset.get(var);
        return null;
    }

    public String getResult() {
        StringBuilder result = new StringBuilder();
        result.append(".data\n");
        for (Asm asm : this.dataSegment)
            result.append(asm.toString());

        result.append("\n.text\n.globl main\n");


        for (Asm asm : this.textSegment)
            if (asm instanceof LabelAsm) result.append(asm.toString());
            else result.append("    ").append(asm.toString());

        return result.toString();
    }
}
