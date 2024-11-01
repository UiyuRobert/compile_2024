package Middle.LLVMIR;

import Middle.LLVMIR.IRTypes.IRArrayType;
import Middle.LLVMIR.IRTypes.IRIntType;
import Middle.LLVMIR.IRTypes.IRType;
import java.util.ArrayList;

/**
 * LLVM IR 全局变量
 * */
public class IRGlobalVariable extends IRValue {
    private boolean isConst;
    private ArrayList<Integer> inits;

    public IRGlobalVariable(IRType type, String name, boolean isConst) {
        super(type, name);
        this.isConst = isConst;
        inits = new ArrayList<>(); // 为空说明非全局且无初始值
    }

    public void setInit(int[] arr) {
        if (arr != null) for (int j : arr) inits.add(j);
    }

    public String getIR() {
        StringBuilder sb = new StringBuilder();
        sb.append("@").append(this.getName());
        sb.append(" = dso_local ");
        if (isConst) sb.append("constant ");
        else sb.append("global ");
        if (this.getType() instanceof IRArrayType) {
            IRType eleType = ((IRArrayType) this.getType()).getElementType();
            int size = ((IRArrayType) this.getType()).getSize();
            sb.append("[").append(size).append(" x ").append(eleType).append("] ");
            if (eleType == IRIntType.getI8()) {
                sb.append("c\"");
                for (Integer init : inits) {
                    if (init != 0)
                        sb.append((char) init.intValue());
                    else sb.append("\\00");
                }
                sb.append("\"");
            }
            else {
                sb.append("[");
                for (Integer init : inits) sb.append(eleType).append(" ").append(init).append(", ");
                sb.append("]");
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
