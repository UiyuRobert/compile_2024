package BackEnd.Assembly;

import BackEnd.MipsBuilder;

public class Asm {
    public Asm() {
        MipsBuilder.getInstance().addAsmInstr(this);
    }
}
