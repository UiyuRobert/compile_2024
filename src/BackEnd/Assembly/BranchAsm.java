package BackEnd.Assembly;

import BackEnd.Register;

/**
 * BEQ（Branch if Equal）用于判断两个寄存器的值是否相等。如果相等，则跳转到指定的目标标签
 * beq $s1, $s2, label
 * $s1 和 $s2：用于比较的两个寄存器
 * label：如果 $s1 == $s2，则跳转到该标签
 *
 * BNE（Branch if Not Equal）用于判断两个寄存器的值是否不相等。如果不相等，则跳转到指定的目标标签
 * bne $s1, $s2, label
 * $s1 和 $s2：用于比较的两个寄存器
 * label：如果 $s1 != $s2，则跳转到该标签
 *
 * BGTZ（Branch if Greater Than Zero）用于判断寄存器的值是否大于零。如果大于零，则跳转到指定的目标标签
 * bgtz $s1, label
 * $s1：用于判断的寄存器。
 * label：如果 $s1 > 0，则跳转到该标签
 *
 * BLEZ（Branch if Less Than or Equal to Zero）用于判断寄存器的值是否小于或等于零。
 * 如果条件满足，则跳转到指定的目标标签
 * blez $s1, label
 * $s1：用于判断的寄存器。
 * label：如果 $s1 <= 0，则跳转到该标签
 *
 * */
public class BranchAsm extends Asm {
    public enum Op {
        BEQ,BNE,BGTZ,BLEZ,BGEZ,BLTZ
    }

    private Op op;
    private Register rs;
    private Register rt;
    private String label;

    // beq, bne
    public BranchAsm(Op op, Register rs, Register rt, String label) {
        this.op = op;
        this.rs = rs;
        this.rt = rt;
        this.label = label;
    }

    // bgtz, bgez, bltz, blez
    public BranchAsm(Op op, Register rs, String label) {
        this.op = op;
        this.rs = rs;
        this.rt = null;
        this.label = label;
    }



    @Override
    public String toString() {
        if (op.ordinal() == Op.BEQ.ordinal() || op.ordinal() == Op.BNE.ordinal()) {
            return op.toString().toLowerCase() + " " + rs + ", " + rt + ", " + label + "\n";
        }
        return op.toString().toLowerCase() + " " + rs + ", " + label + "\n";
    }
}
