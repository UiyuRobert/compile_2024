package Middle.LLVMIR.IRTypes;

/**
 * 整数类型，既是 llvm 语句的返回值类型，也当作 GL 的具体数据类型使用
 * */
public class IRIntType implements IRType {
    private int bits; // 位数
    private static final IRIntType I64 = new IRIntType(64);
    private static final IRIntType I32 = new IRIntType(32); // int
    private static final IRIntType I8 = new IRIntType(8); // char
    private static final IRIntType I1 = new IRIntType(1); // bool

    private IRIntType(int bits){
        this.bits = bits;
    }

    public static IRIntType I64() { return I64; }

    public static IRIntType I32() { return I32; }

    public static IRIntType I8() { return I8; }

    public static IRIntType I1() { return I1; }

    @Override
    public String toString() {
        if(bits == 32) return "i32";
        else if(bits == 8) return "i8";
        else if(bits == 64) return "i64";
        else return "i1";
    }
}
