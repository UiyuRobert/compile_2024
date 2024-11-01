package Middle.LLVMIR;

import java.util.ArrayList;

public class IRModule{
    private ArrayList<IRGlobalVariable> globals; // 全局变量
    private ArrayList<IRFunction> functions;

    public IRModule() {
        globals = new ArrayList<>();
        functions = new ArrayList<>();
    }

    public void addGlobalVariable(IRGlobalVariable globalVar) {
        globals.add(globalVar);
    }

    public void addFunction(IRFunction function) {
        functions.add(function);
    }

    public String getIR() {
        StringBuilder sb = new StringBuilder();
        for (IRGlobalVariable gv : globals) sb.append(gv.getIR());
        return sb.toString();
    }
}
