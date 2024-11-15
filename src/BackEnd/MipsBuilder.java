package BackEnd;

import BackEnd.Assembly.Asm;
import BackEnd.Assembly.GlobalVarAsm;
import BackEnd.Assembly.LabelAsm;

import java.util.ArrayList;

public class MipsBuilder {
    private static MipsBuilder mipsBuilder = new MipsBuilder();
    public static MipsBuilder getInstance() { return  mipsBuilder; }

    // 指令
    private ArrayList<Asm> dataSegment;
    private ArrayList<Asm> textSegment;

    private MipsBuilder() {
        this.dataSegment = new ArrayList<>();
        this.textSegment = new ArrayList<>();
    }

    public void addAsmInstr(Asm asm) {
        if (asm instanceof GlobalVarAsm)
            this.dataSegment.add(asm);
        else
            this.textSegment.add(asm);
    }

    public String getResult() {
        StringBuilder result = new StringBuilder();
        result.append(".data\n");
        for (Asm asm : this.dataSegment)
            result.append(asm.toString());

        result.append("\n.text\n");

        for (Asm asm : this.textSegment)
            if (asm instanceof LabelAsm) result.append(asm.toString());
            else result.append("    ").append(asm.toString());

        return result.toString();
    }
}
