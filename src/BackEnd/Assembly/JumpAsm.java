package BackEnd.Assembly;

import BackEnd.Register;

/**
 * j 指令用于进行无条件跳转, j label
 * jr 指令用于跳转到寄存器中存储的地址，常用于实现函数返回, jr $ra
 * jal 指令用于调用函数，并且将返回地址保存在寄存器 $ra, jal label
 * */
public class JumpAsm extends Asm {
    public enum Op {
        J, JAL, JR
    }

    private Op op;
    private String target;
    private Register rd;

    public JumpAsm(Op op, String target) {
        this.op = op;
        this.target = target;
        this.rd = null;
    }


    public JumpAsm(Op op, Register rd) {
        this.op = op;
        this.target = null;
        this.rd = rd;
    }

    @Override
    public String toString() {
        if (op.ordinal() == Op.JR.ordinal()) {
            return op.toString().toLowerCase() + " " + rd + "\n";
        }
        return op.toString().toLowerCase() + " " + target + "\n";
    }
}