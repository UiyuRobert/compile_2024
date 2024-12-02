package BackEnd.Assembly;

import BackEnd.Register;

/**
 * la 指令：将标签（例如全局变量、数组、字符串等）的内存地址加载到指定的寄存器中
 * la $register, label
 * $register：目标寄存器，用于存储地址
 * label：数据段中定义的标签，指向需要加载地址的内存位置
 * */
public class LaAsm extends Asm {
    private Register target;
    private String label;

    public LaAsm(Register target, String label) {
        this.target = target;
        this.label = label;
    }

    @Override
    public String toString() {
        return "la " + target + ", " + label + "\n";
    }
}
