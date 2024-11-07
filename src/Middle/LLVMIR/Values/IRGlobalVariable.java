package Middle.LLVMIR.Values;

import Middle.LLVMIR.IRTypes.IRArrayType;
import Middle.LLVMIR.IRTypes.IRIntType;
import Middle.LLVMIR.IRTypes.IRPtrType;
import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRUse;
import Middle.LLVMIR.IRValue;

import java.util.ArrayList;

/**
 * LLVM IR 全局变量
 * */
public class IRGlobalVariable extends IRValue {
    private boolean isConst;
    private ArrayList<Integer> inits;
    private int length;
    private IRType elementTy;

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
        this.content = content;
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
        sb.append("c").append(content).append(", align 1\n");
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
                        if (init != 0)
                            sb.append((char) init.intValue());
                        else sb.append("\\00");
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
}
