package BackEnd.Assembly;

import BackEnd.Register;

/**
 * mult $s1, $s2
 * 将 $s1 和 $s2 的值相乘，结果是一个 64 位的乘积，其中：
 * 低 32 位存入 LO 寄存器。
 * 高 32 位存入 HI 寄存器。
 *
 * div $s1, $s2
 * 将 $s1 除以 $s2，结果的商存入 LO 寄存器，余数存入 HI 寄存器。
 * */
public class MulDivAsm extends Asm {
    public enum Op {
        MULT, DIV
    }


    private Op op;
    private Register rs;
    private Register rt;

    public MulDivAsm(Op op, Register rs, Register rt) {
        this.op = op;
        this.rs = rs;
        this.rt = rt;
    }

    @Override
    public String toString() {
        return op.toString().toLowerCase() + " " + rs + ", " + rt + "\n";
    }
}
