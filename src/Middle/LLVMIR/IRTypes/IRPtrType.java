package Middle.LLVMIR.IRTypes;

/**
 * 指针类型, pointed 为指向的类型
 * */
public class IRPtrType implements IRType {
    private IRType pointed;

    public IRPtrType(IRType pointed) {
        this.pointed = pointed;
    }
}
