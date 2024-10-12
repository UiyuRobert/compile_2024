package Middle.Symbols;

import java.util.ArrayList;
import java.util.List;

public class FuncSymbol extends Symbol {
    private List<Symbol> args;

    public FuncSymbol(Type type, String name, int lineNumber) {
        super(type, name, lineNumber);
        args = new ArrayList<>();
    }

    public void addArg(Symbol arg) {
        args.add(arg);
    }
}
