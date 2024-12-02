package BackEnd.Assembly;

import BackEnd.Register;

/**
 * mfhi（move from HI）：将 HI 寄存器中的值复制到通用寄存器中
 * mfhi $d
 * $d 是目标寄存器，接收 HI 寄存器中的值
 *
 * mflo（move from LO）：将 LO 寄存器中的值复制到通用寄存器中
 * mflo $d
 * $d 是目标寄存器，接收 LO 寄存器中的值
 * */
public class HiLoAsm extends Asm {
    public enum Op {
        MFHI, MFLO, MTHI, MTLO
    }

    private Op op;
    private Register rd;

    public HiLoAsm(Op op, Register rd) {
        this.op = op;
        this.rd = rd;
    }

    @Override
    public String toString() {
        return op.toString().toLowerCase() + " " + rd + "\n";
    }
}
