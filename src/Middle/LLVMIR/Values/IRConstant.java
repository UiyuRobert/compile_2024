package Middle.LLVMIR.Values;

import Middle.LLVMIR.IRTypes.IRType;
import Middle.LLVMIR.IRValue;

public class IRConstant extends IRValue {
    private int value;

    public IRConstant(IRType type, int value) {
        super(type, String.valueOf(value));
        this.value = value;
    }

    public int getValue() { return value; }
}
