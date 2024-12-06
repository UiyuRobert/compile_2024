package Middle.LLVMIR.Values.Instructions.Terminal;

import BackEnd.Assembly.BranchAsm;
import BackEnd.Assembly.CommentAsm;
import BackEnd.Assembly.JumpAsm;
import BackEnd.Assembly.MemAsm;
import BackEnd.MipsBuilder;
import BackEnd.Register;
import Middle.LLVMIR.IRTypes.IRIntType;
import Middle.LLVMIR.IRTypes.IRVoidType;
import Middle.LLVMIR.IRUse;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.IRBasicBlock;
import Middle.LLVMIR.Values.Instructions.IRInstrType;
import Middle.LLVMIR.Values.Instructions.IRInstruction;
import Middle.LLVMIR.Values.Instructions.IRLabel;

/**
 *  `br i1 <cond>, label <iftrue>, label <iffalse>`
 *  `br label <dest>`
 *
 * */
public class IRBr extends IRInstruction {
    private IRValue cond;
    private IRLabel trueLabel;
    private IRLabel falseLabel;

    private IRLabel dest;

    public IRBr(IRValue cond, IRLabel trueLabel, IRLabel falseLabel) {
        super(IRInstrType.Br, IRVoidType.Void(), 3);
        this.cond = cond;
        this.trueLabel = trueLabel;
        this.trueLabel.addJumpNumber();
        this.falseLabel = falseLabel;
        this.falseLabel.addJumpNumber();
        this.dest = null;
        IRUse use1 = new IRUse(this, cond, 0);
        this.addUse(use1);
        cond.addUse(use1);
        IRUse use2 = new IRUse(this, trueLabel, 1);
        this.addUse(use2);
        trueLabel.addUse(use2);
        IRUse use3 = new IRUse(this, falseLabel, 2);
        this.addUse(use3);
        falseLabel.addUse(use3);
        if (cond.getType() != IRIntType.I1())
            System.out.println("WTF !!! BR COND NOT I1");
    }

    public IRBr(IRLabel dest) {
        super(IRInstrType.Br, IRVoidType.Void(), 1);
        this.dest = dest;
        this.dest.addJumpNumber();
        IRUse use = new IRUse(this, dest, 0);
        this.addUse(use);
        dest.addUse(use);
    }

    public boolean noCondition() { return dest != null; }

    public IRBasicBlock getTargetBlock(String kind) {
        if (kind.equals("true"))
            return trueLabel.getBelongsTo();
        else if (kind.equals("false"))
            return falseLabel.getBelongsTo();
        else
            return dest.getBelongsTo();
    }

    public String getIR() {
        if (this.getOperandCount() == 1) return "br label " + dest.getName() + "\n";
        else {
            StringBuilder sb = new StringBuilder();
            sb.append("br i1 ").append(cond.getName());
            sb.append(", label ").append(trueLabel.getName());
            sb.append(", label ").append(falseLabel.getName()).append("\n");
            return sb.toString();
        }
    }

    public void toAssembly() {
        /* TODO*/ // 只从栈中取值
        new CommentAsm(this.getIR());
        MipsBuilder builder = MipsBuilder.builder();
        Register condReg = Register.K0; // 放 I1 条件

        if (dest == null) {
            // br i1 <cond>, label <iftrue>, label <iffalse>
            int condOffset = builder.getVarOffsetInStack(cond);
            new MemAsm(MemAsm.Op.LW, condReg, Register.SP, condOffset);

            // 如果 cond != 0 则跳转到 trueLabel
            new BranchAsm(BranchAsm.Op.BNE, condReg, Register.ZERO, trueLabel.getMipsName());
            // 否则跳转到 falseLabel
            new JumpAsm(JumpAsm.Op.J, falseLabel.getMipsName());
        } else {
            // br label <dest>
            new JumpAsm(JumpAsm.Op.J, dest.getMipsName());
        }
    }
}
