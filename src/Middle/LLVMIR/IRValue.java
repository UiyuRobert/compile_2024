package Middle.LLVMIR;

import Middle.LLVMIR.IRTypes.IRType;

import java.io.*;
import java.util.ArrayList;

public class IRValue implements Serializable {
    private IRType type; // 值类型
    private String name;
    private ArrayList<IRUse> useList;

    private boolean isParam = false; // 是否是函数的参数
    private boolean isAlloc = false;

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

    public void setName(String name) { this.name = name; }

    public void setParam(boolean isParam) { this.isParam = isParam; }

    public boolean isParam() { return isParam; }

    public void setAlloc(boolean isAlloc) { this.isAlloc = isAlloc; }

    public boolean isAlloc() { return isAlloc; }

    public ArrayList<IRUse> getUseList() { return useList; }

    public IRValue deepClone() {
        IRValue cloneVal = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = null;
            oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            // 反序列化
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            cloneVal = (IRValue) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return cloneVal;
    }
}
