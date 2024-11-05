package Middle.LLVMIR.Values;

import Middle.LLVMIR.IRTypes.IRArrayType;
import Middle.LLVMIR.IRTypes.IRIntType;
import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRValue;

import java.util.ArrayList;

/**
 * LLVM IR 全局变量
 * */
public class IRGlobalVariable extends IRValue {
    private boolean isConst;
    private ArrayList<Integer> inits;
    private int length;

    public IRGlobalVariable(IRType type, String name, boolean isConst) {
        super(type, "@" + name);
        this.isConst = isConst;
        inits = new ArrayList<>(); // 为空说明非全局且无初始值
        length = 0;
    }

    public void setLength(int length) { this.length = length; }

    public void setInit(int[] arr) {
        if (arr != null) for (int j : arr) inits.add(j);
    }

    public String getIR() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName());
        sb.append(" = dso_local ");
        if (isConst) sb.append("constant ");
        else sb.append("global ");
        if (this.getType() instanceof IRArrayType) {
            IRType eleType = ((IRArrayType) this.getType()).getElementType();
            sb.append(this.getType().toString()).append(" ");
            if (eleType == IRIntType.getI8()) {
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
            sb.append(this.getType()).append(" ");
            if (!inits.isEmpty()) sb.append(inits.get(0));
            else sb.append("0");
        }
        sb.append("\n");
        return sb.toString();
    }
}
