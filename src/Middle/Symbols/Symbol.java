package Middle.Symbols;

/**
 * 符号类型
 * */
public class Symbol {
    public enum Type {
        ConstChar, ConstInt, ConstCharArray, ConstIntArray, // 常量
        Char, Int, CharArray, IntArray, // 变量
        VoidFunc, CharFunc, IntFunc // 函数
    }
    private Type type;
    private String name;
    private int lineNumber;

    public Symbol(Type type, String name, int lineNumber) {
        this.name = name;
        this.type = type;
        this.lineNumber = lineNumber;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
