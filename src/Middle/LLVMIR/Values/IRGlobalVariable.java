package Middle.LLVMIR.Values;

import BackEnd.Assembly.GlobalVarAsm;
import BackEnd.Assembly.LiAsm;
import BackEnd.Assembly.MemAsm;
import BackEnd.Register;
import Middle.LLVMIR.IRTypes.IRArrayType;
import Middle.LLVMIR.IRTypes.IRIntType;
import Middle.LLVMIR.IRTypes.IRPtrType;
import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRValue;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * LLVM IR 全局变量
 * */
public class IRGlobalVariable extends IRValue {
    private boolean isConst;
    private ArrayList<Integer> inits;
    private int length;
    private IRType elementTy;

    private static HashMap<Integer, String> ascii = new HashMap<>(){{
        put(7, "\\07");
        put(8, "\\08");
        put(9, "\\09");
        put(10, "\\0A");
        put(11, "\\0B");
        put(12, "\\0C");
        put(34, "\\22");
        put(39, "\\27");
        put(92, "\\5C");
        put(0, "\\00");
    }};

    private static int privateCount = 0;
    private boolean isPrivate;
    private String content;

    public IRGlobalVariable(IRType type, String name, boolean isConst) {
        super(new IRPtrType(type), "@" + name);
        elementTy = type;
        this.isConst = isConst;
        inits = new ArrayList<>(); // 为空说明非全局且无初始值
        isPrivate = false;
        length = 0;
    }

    public IRGlobalVariable(IRType type, String content) {
        super(new IRPtrType(type));
        isPrivate = true;
        elementTy = type;
        String name = privateCount == 0 ? "@.str" : "@.str." + privateCount;
        privateCount++;
        this.setName(name);
        this.content = content.replace("\n", "\\0A");
    }

    public void setLength(int length) { this.length = length; }

    public void setInit(Integer[] arr) {
        if (arr != null) for (int j : arr) inits.add(j);
    }

    public String getIR() {
        if (isPrivate) return getPrivate();
        else return getNotPrivate();
    }

    public String getPrivate() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName()).append(" = ");
        sb.append("private unnamed_addr constant ");
        sb.append(elementTy.toString()).append(" ");
        sb.append("c\"").append(content).append("\", align 1\n");
        return sb.toString();
    }

    public String getNotPrivate() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName());
        sb.append(" = dso_local ");
        if (isConst) sb.append("constant ");
        else sb.append("global ");
        if (elementTy instanceof IRArrayType) {
            IRType eleType = ((IRArrayType) elementTy).getElementType();
            sb.append(elementTy.toString()).append(" ");
            if (eleType == IRIntType.I8()) {
                if (inits.isEmpty() && length != 0)
                    sb.append("zeroinitializer");
                else {
                    sb.append("c\"");
                    for (Integer init : inits) {
                        if (ascii.containsKey(init))
                            sb.append(ascii.get(init));
                        else sb.append((char) init.intValue());
                    }
                    sb.append("\"");
                }
                sb.append(", align 1");
            }
            else {
                if (inits.isEmpty() && length != 0)
                    sb.append("zeroinitializer");
                else {
                    sb.append("[ ");
                    sb.append(eleType).append(" ").append(inits.get(0));
                    for (int i = 1; i < inits.size(); i++) {
                        sb.append(", ").append(eleType).append(" ").append(inits.get(i));
                    }
                    sb.append(" ]");
                }
            }
        } else {
            sb.append(elementTy).append(" ");
            if (!inits.isEmpty()) sb.append(inits.get(0));
            else sb.append("0");
        }
        sb.append("\n");
        return sb.toString();
    }

    public void toAssembly() {
        if (isPrivate) {
            String name = getName().substring(2); // 去掉 @.
            new GlobalVarAsm.Asciiz(name, content);
        } else if (elementTy == IRIntType.I8()) {
            String name = getName().substring(1); // 去掉 @
            int initVal = inits.isEmpty() ? 0 : inits.get(0);
            new GlobalVarAsm.Byte(name, initVal);
        } else if (elementTy == IRIntType.I32()) {
            String name = getName().substring(1);
            int initVal = inits.isEmpty() ? 0 : inits.get(0);
            new GlobalVarAsm.Word(name, initVal);
        } else { // 数组部分
            IRArrayType irType = (IRArrayType) elementTy;
            if (irType.getElementType() == IRIntType.I32())
                processArray(4, irType.getSize());
            else if (irType.getElementType() == IRIntType.I8())
                processArray(1, irType.getSize());
            else {
                System.out.println("WTF???? GL ARRAY TO ASM");
            }
        }
    }

    private boolean isNeedInitial() {
        for (Integer init : inits)
            if (init != 0) return true;
        return false;
    }

    private void processArray(int elementSize, int arraySize) {
        String name = getName().substring(1);
        new GlobalVarAsm.Space(name, arraySize * elementSize);
        if (isNeedInitial()) {
            int offset = 0;
            for (Integer init : inits) {
                if (init == 0) continue;
                new LiAsm(Register.T0, init);
                new MemAsm(MemAsm.Op.SW, Register.T0, name, offset);
                offset += elementSize;
            }
        }
    }
}
