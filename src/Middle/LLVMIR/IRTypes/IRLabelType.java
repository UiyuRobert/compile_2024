package Middle.LLVMIR.IRTypes;

public class IRLabelType implements IRType {
    private static IRLabelType Label = new IRLabelType();

    private IRLabelType(){}

    public static IRLabelType getLabel(){ return Label; }

    @Override
    public int getByteSize() {
        System.out.println("NOT LABEL !!!");
        return -1;
    }
}
