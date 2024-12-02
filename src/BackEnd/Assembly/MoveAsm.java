package BackEnd.Assembly;

import BackEnd.Register;

/**
 * move 指令用于将一个寄存器的值复制到另一个寄存器中
 * move $dest, $src
 * $dest：目标寄存器，用于接收被复制的数据
 * $src：源寄存器，提供要复制的数据
 * */
public class MoveAsm extends Asm {
    private Register dst;
    private Register src;

    public MoveAsm(Register dst, Register src) {
        this.dst = dst;
        this.src = src;
    }

    @Override
    public String toString() {
        return "move " + dst + ", " + src + "\n";
    }
}
