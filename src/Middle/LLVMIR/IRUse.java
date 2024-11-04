package Middle.LLVMIR;

public class IRUse {
    private int operandPst; // 操作数位置，靠前的为 1
    private IRUser user;
    private IRValue value;

    public IRUse(IRUser user, IRValue value, int operandPst) {
        this.user = user;
        this.value = value;
        this.operandPst = operandPst;
    }
}
