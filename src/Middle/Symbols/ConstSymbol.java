package Middle.Symbols;

import java.util.List;

public class ConstSymbol extends Symbol {
    private int value; // char / int
    private int[] arrayValue;

    public ConstSymbol(Type type, String name, int lineNumber) {
        super(type, name, lineNumber);
    }

    public void setInitValue(int value) {
        this.value = value;
    }

    public void setInitValue(int[] arrayValue) {
        this.arrayValue = arrayValue;
    }

    public int getValue() { return value; }

    public int getValue(int index) { return arrayValue[index]; }
}
