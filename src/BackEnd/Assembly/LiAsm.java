package BackEnd.Assembly;

import BackEnd.Register;

/**
 * li $register, immediate_value
 * $register：目标寄存器，用于存放立即数的寄存器。
 * immediate_value：要加载的立即数，可以是一个正整数或负整数，通常是 16 位或更小的数值。
 * li（load immediate）指令用于将一个立即数（literal immediate value）加载到寄存器中
 * */
public class LiAsm extends Asm {
    private Register rd;
    private Integer number;

    public LiAsm(Register rd, Integer number) {
        this.rd = rd;
        this.number = number;
    }

    @Override
    public String toString() {
        return "li " + rd + " " + number + "\n";
    }
}
