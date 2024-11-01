package Middle.LLVMIR;

import Middle.LLVMIR.IRTypes.IRType;

import java.util.ArrayList;

public class IRValue {
    private IRType type; // 值类型
    private String name;
    private ArrayList<IRUse> useList;

    public IRValue() {}

    public IRValue(IRType type, String name) {
        this.type = type;
        this.name = name;
    }

    public IRType getType() { return type; }

    public String getName() { return name; }
}
