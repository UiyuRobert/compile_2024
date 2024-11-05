package Middle.LLVMIR;

import Middle.LLVMIR.IRTypes.IRType;

import java.util.ArrayList;

public class IRValue {
    private IRType type; // 值类型
    private String name;
    private ArrayList<IRUse> useList;

    public IRValue(IRType type, String name) {
        this.type = type;
        this.name = name;
        this.useList = new ArrayList<>();
    }

    public IRValue(IRType type) {
        this.type = type;
        this.name = "";
        this.useList = new ArrayList<>();
    }

    public IRType getType() { return type; }

    public String getName() { return name; }

    public void addUse(IRUse use) { useList.add(use); }


}
