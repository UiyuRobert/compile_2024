package Middle.LLVMIR.IRTypes;

import Middle.LLVMIR.IRValue;
import java.util.ArrayList;

public class IRFuncType implements IRType {
    private IRType returnType;
    private ArrayList<IRValue> parameters;

    public IRFuncType(IRType returnType){
        this.returnType = returnType;
        this.parameters = new ArrayList<>();
    }

    public void addParam(IRValue param){
        parameters.add(param);
    }

    public IRType getReturnType(){
        return returnType;
    }

    public ArrayList<IRValue> getParameters(){
        return parameters;
    }

    @Override
    public int getByteSize() {
        System.out.println("NOT FUNC !!!");
        return -1;
    }
}
