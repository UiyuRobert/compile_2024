package Middle.LLVMIR.IRTypes;

public class IRVoidType implements IRType {
    private static final IRVoidType VOID = new IRVoidType();
    private IRVoidType(){ }

    public static IRVoidType Void(){ return VOID; }

    @Override
    public String toString() {
        return "void";
    }

    @Override
    public int getByteSize() {
        System.out.println("NOT VOID!!!");
        return -1;
    }
}
