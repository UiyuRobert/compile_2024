package Middle.LLVMIR.IRTypes;

public class IRVoidType implements IRType {
    private static final IRVoidType VOID = new IRVoidType();
    private IRVoidType(){ }

    public static IRVoidType getVoid(){ return VOID; }

    @Override
    public String toString() {
        return "void";
    }
}
