package Middle.LLVMIR;

import Middle.LLVMIR.IRTypes.IRType;

import java.io.*;

public class IRUser extends IRValue implements Serializable {
    private int operandCnt; // 操作数数量

    public IRUser(IRType type, int operandCnt) {
        super(type);
        this.operandCnt = operandCnt;
    }

    public IRValue getOperand(int i) {
        /* TODO */
        return null;
    }

    public IRUser deepClone() {
        IRUser cloneUser = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = null;
            oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            // 反序列化
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            cloneUser = (IRUser) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return cloneUser;
    }
}
