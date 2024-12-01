package BackEnd.Assembly;

import BackEnd.MipsBuilder;

public class Asm {
    public Asm() {
        MipsBuilder.builder().addAsmInstr(this);
    }
}
