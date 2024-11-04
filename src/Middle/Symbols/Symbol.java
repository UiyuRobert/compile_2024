package Middle.Symbols;

import Middle.LLVMIR.IRValue;

/**
 * 符号类型
 * */
public class Symbol {
    public enum Type {
        ConstChar, ConstInt, ConstCharArray, ConstIntArray, // 常量
        Char, Int, CharArray, IntArray, NotArray,// 变量
        VoidFunc, CharFunc, IntFunc, // 函数
        NONE // 空类型
    }
    private Type type;
    private String name;
    private int lineNumber;
    private IRValue value;

    public Symbol(Type type, String name, int lineNumber) {
        this.name = name;
        this.type = type;
        this.lineNumber = lineNumber;
        value = null;
    }

    public Type getType() {
        return type;
    }

    public void setIRValue(IRValue value) { this.value = value; }

    public Type getRefType(boolean isValInArray) {
        switch (type) {
            case ConstChar:
            case Char:
            case Int:
            case ConstInt:
                return Type.NotArray;
            case ConstCharArray:
            case CharArray:
                if (isValInArray) return Type.NotArray;
                return Type.CharArray;
            case ConstIntArray:
            case IntArray:
                if (isValInArray) return Type.NotArray;
                return Type.IntArray;
            default:
                return Type.NONE;
        }
    }

    public String getName() {
        return name;
    }

    public int getLineNumber() { return lineNumber; }
}
