package Middle.LLVMIR.IRTypes;

/**
 * 数组类型，当作 GL 的具体数据类型使用
 * */
public class IRArrayType implements IRType {
    private IRType elementType;
    private int size; // 为 -1 时说明是函数的形参

    public IRArrayType(IRType elementType, int size){
        this.elementType = elementType;
        this.size = size;
    }

    public IRType getElementType(){ return elementType; }

    public int getSize(){ return size; }

    @Override
    public String toString(){
        if (elementType == IRIntType.I32())
            return "[" + size + " x i32]";
        return "[" + size + " x i8]";
    }

    @Override
    public int getByteSize() {
        if (elementType == IRIntType.I32())
            return size * 4;
        return size;
    }
}
