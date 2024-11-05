package Middle.LLVMIR.Values;

import Middle.LLVMIR.IRTypes.IRArrayType;
import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRValue;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 代码中的常量值，针对于局部变量
 * */
public class IRConstArray extends IRValue {
    private ArrayList<Integer> inits;

    public IRConstArray(IRType type, String name) {
        super(type, name);
        inits = new ArrayList<>();
    }

    public void setInit(Integer[] arr) {
        if (arr != null) inits.addAll(Arrays.asList(arr));
    }

    public IRValue getValByIndex(int index) {
        IRType type = ((IRArrayType) getType()).getElementType();
        return new IRConstant(type, inits.get(index));
    }

    public int size() {
        return inits.size();
    }
}
