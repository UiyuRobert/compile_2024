package Middle.LLVMIR.Values;

import Middle.LLVMIR.IRTypes.IRArrayType;
import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRValue;

import java.util.ArrayList;

/**
 * 代码中的常量值，针对于局部变量
 * */
public class IRConstArray extends IRValue {
    private ArrayList<Integer> inits;

    public IRConstArray(IRType type, String name) {
        super(type, name);
        inits = new ArrayList<>();
    }

    public void setInit(int[] arr) {
        if (arr != null) for (int j : arr) inits.add(j);
    }

    public IRValue getValByIndex(int index) {
        IRType type = ((IRArrayType) getType()).getElementType();
        return new IRConstant(type, inits.get(index));
    }

    public int size() {
        return inits.size();
    }
}
