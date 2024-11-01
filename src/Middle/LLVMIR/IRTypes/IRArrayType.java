package Middle.LLVMIR.IRTypes;

public class IRArrayType implements IRType {
    private IRType elementType;
    private int size;

    public IRArrayType(IRType elementType, int size){
        this.elementType = elementType;
        this.size = size;
    }

    public IRType getElementType(){ return elementType; }

    public int getSize(){ return size; }
}
