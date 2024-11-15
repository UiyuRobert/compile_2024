package BackEnd.Assembly;

import BackEnd.Register;

/**
 * lw $t0, offset($t1)
 * sw $t0, offset($t1)
 * $t0：寄存器，存储从内存中加载的数据，或要存储的数据来源。
 * offset($t1)：表示内存地址。$t1 是基址寄存器，offset 是偏移量，二者结合可以表示一个内存中的具体地址。
 * */
public class MemAsm extends Asm {
    public enum Op {
        // load
        LW, LH, LHU, LB, LBU,
        // store
        SW, SH, SB
    }

    private Op op;
    private Register rd;
    private Register base;
    private Integer offset;
    private String label;

    public MemAsm(Op op, Register rd, Register base, Integer offset) {
        this.op = op;
        this.rd = rd;
        this.base = base;
        this.offset = offset;
        this.label = null;
    }

    public MemAsm(Op op, Register rd, String label, Integer offset) {
        this.op = op;
        this.rd = rd;
        this.base = null;
        this.offset = offset;
        this.label = label;
    }

    @Override
    public String toString() {
        if (label == null) {
            return op.toString().toLowerCase() + " " + rd + " " + offset + "(" + base + ")\n";
        }
        return op.toString().toLowerCase() + " " + rd + " " + label + "+" + offset + "\n";
    }
}
