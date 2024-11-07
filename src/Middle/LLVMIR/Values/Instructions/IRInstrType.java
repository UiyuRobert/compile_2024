package Middle.LLVMIR.Values.Instructions;

public enum IRInstrType {
    /* Arithmetic Binary */
    Add,// +
    Sub,// -
    Mul,// *
    Sdiv,// / 有符号除法
    Srem,// % 有符号取余
    Bitand, // bitand
    /* Logic Binary */
    Lt, // <
    Le, // <=
    Ge, // >=
    Gt, // >
    Eq, // ==
    Ne, // !=
    And,// &
    Or, // |
    Not, // ! ONLY ONE PARAM
    Beq, // IrBeq branch if ==
    Bne, // IrBne branch if !=
    Blt, // IrBlt branch if less than <
    Ble, // IrBle branch if less or equal <=
    Bgt, // IrBgt branch if greater than >
    Bge, // IrBge branch if greater or equal >=
    Goto, // IrGoto
    /* Terminator */
    Br,
    Call,
    Ret,
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
