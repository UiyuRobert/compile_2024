package Middle.LLVMIR;

import java.io.*;
import java.util.ArrayList;

public class IRUse implements Serializable {
    private static ArrayList<IRUse> uses = new ArrayList<>();
    private int operandPst; // 操作数位置，靠前的为 0
    private IRUser user;
    private IRValue value;

    public IRUse(IRUser user, IRValue value, int operandPst) {
        this.user = user;
        this.value = value;
        this.operandPst = operandPst;
        uses.add(this);
    }

    public boolean isPstOperand(IRUser user, int operandPst) {
        return this.user == user && this.operandPst == operandPst;
    }

    public IRValue getUsee() {
        return value;
    }

    public void setUsee(IRValue value) { this.value = value; }

    public static ArrayList<IRUse> getUses() { return uses; }

    public IRUse deepClone() {
        IRUse cloneUse = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = null;
            oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            // 反序列化
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            cloneUse = (IRUse) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return cloneUse;
    }
}
