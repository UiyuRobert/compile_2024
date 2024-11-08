package Middle.LLVMIR.Values.Instructions;

public enum IRInstrType {
    /* Arithmetic Binary */
    Add,// +
    Sub,// -
    Mul,// *
    Sdiv,// / 有符号除法
    Srem,// % 有符号取余
    /* Logic Binary */
    //  icmp
    Slt, // <
    Sle, // <=
    Sge, // >=
    Sgt, // >
    Eq, // ==
    Ne, // !=

    And,// &&
    Or, // ||
    Not, // ! ONLY ONE PARAM
    Goto, // IrGoto
    /* Terminator */
    Br,
    Ret,
    /* function call */
    Call,
    /* mem op */
    Alloca,
    Load,
    Store,
    GEP, // Get Element Ptr
    Zext,
    Trunc,
    Phi,//用于 mem2reg
    /* label */
    Label,
}
