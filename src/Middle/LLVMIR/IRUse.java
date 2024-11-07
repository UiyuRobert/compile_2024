package Middle.LLVMIR;

import java.io.*;

public class IRUse implements Serializable {
    private int operandPst; // 操作数位置，靠前的为 0
    private IRUser user;
    private IRValue value;

    public IRUse(IRUser user, IRValue value, int operandPst) {
        this.user = user;
        this.value = value;
        this.operandPst = operandPst;
    }

    public boolean isPstOperand(IRUser user, int operandPst) {
        return this.user == user && this.operandPst == operandPst;
    }

    public IRValue getBUsed() {
        return value;
    }

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
