package Middle.LLVMIR.IRTypes;

import Middle.LLVMIR.IRValue;

/**
 * 指针类型, pointed 为指向的类型
 * */
public class IRPtrType implements IRType {
    private IRType pointed;
    private IRValue pointVal; // 所指的对象

    public IRPtrType(IRType pointed) {
        this.pointed = pointed;
        pointVal = null;
    }

    public IRPtrType(IRValue pointVal) {
        this.pointVal = pointVal;
        pointed = pointVal.getType();
    }

    public IRType getPointed() { return pointed; }

    @Override
    public String toString() {
        return pointed.toString() + "*";
    }

    @Override
    public int getByteSize() { return 4; }

    public int getPointedByteSize() {
        if (pointed == IRIntType.I8()) return 1;
        else if (pointed == IRIntType.I32()) return 4;
        else if (pointed instanceof IRArrayType) return pointed.getByteSize();
        System.out.println("BYTES SIZE OF PTR WRONG !!!");
        return -1;
    }
}
