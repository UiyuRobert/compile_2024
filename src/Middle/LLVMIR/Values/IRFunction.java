package Middle.LLVMIR.Values;

import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRValue;

import java.util.ArrayList;

public class IRFunction extends IRValue {
    private static int counter = 0;
    private ArrayList<IRBasicBlock> blocks; // 函数内部基本块集合

    public IRFunction(IRType type, String name) {
        super(type, name); // 此处的 Type 是函数返回值类型
        counter = 0;
        blocks = new ArrayList<>();
    }

    /* 计算变量的下标 */
    public static int getCounter() {
        return counter++;
    }
}
