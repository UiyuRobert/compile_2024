package BackEnd.Assembly;

import BackEnd.Register;

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
        return op.toString().toLowerCase() + " " + rs + " " + rt + "\n";
    }
}
