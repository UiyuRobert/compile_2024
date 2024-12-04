package Middle.LLVMIR;

import BackEnd.Assembly.CommentAsm;
import BackEnd.Assembly.JumpAsm;
import BackEnd.Assembly.LabelAsm;
import Middle.LLVMIR.Values.IRFunction;
import Middle.LLVMIR.Values.IRGlobalVariable;

import java.util.ArrayList;

public class IRModule{
    private ArrayList<IRGlobalVariable> globals; // 全局变量
    private ArrayList<IRGlobalVariable> strPrivate;
    private ArrayList<IRFunction> functions;

    public IRModule() {
        globals = new ArrayList<>();
        functions = new ArrayList<>();
        strPrivate = new ArrayList<>();
    }

    public void addGlobalVariable(IRGlobalVariable globalVar) {
        globals.add(globalVar);
    }

    public void addFunction(IRFunction function) {
        functions.add(function);
    }

    public void addStrPrivate(IRGlobalVariable strVar) { strPrivate.add(strVar); }

    public String getIR() {
        StringBuilder sb = new StringBuilder();
        sb.append("declare i32 @getint()\n");
        sb.append("declare i32 @getchar()\n");
        sb.append("declare void @putint(i32)\n");
        sb.append("declare void @putchar(i32)\n");
        sb.append("declare void @putstr(i8*)\n\n");
        for (IRGlobalVariable gv : globals) sb.append(gv.getIR());
        sb.append("\n");
        for (IRGlobalVariable gv : strPrivate) sb.append(gv.getIR());
        sb.append("\n");
        for (IRFunction f : functions) sb.append(f.getIR());
        return sb.toString();
    }

    public void toAssembly() {
        for (IRGlobalVariable gv : globals) gv.toAssembly();
        for (IRGlobalVariable gv : strPrivate) gv.toAssembly();

        new JumpAsm(JumpAsm.Op.JAL, "main");
        new JumpAsm(JumpAsm.Op.J, "end");
        for (IRFunction f : functions) f.toAssembly();

        new LabelAsm("end");
    }
}
