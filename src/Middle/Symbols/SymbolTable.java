package Middle.Symbols;

import ErrorHandling.ErrorHandling;

import java.util.ArrayList;
import java.util.HashMap;
import static Middle.Visitor.curTable;

/**
 * 符号表
 * */
public class SymbolTable {
    public static ArrayList<String> record = new ArrayList<>();
    private int scopeNumber;
    private SymbolTable parent;
    private HashMap<String, Symbol> symbols;

    public SymbolTable(SymbolTable parent, int scopeNumber) {
        this.parent = parent;
        this.symbols = new HashMap<>();
        this.scopeNumber = scopeNumber;
    }

    public Symbol getSymbol(String symbolName, int lineNumber) {
        SymbolTable table = curTable;
        while (!table.hasSymbolCur(symbolName) && table.getParent() != null) {
            table = table.getParent();
        }
        if (table.hasSymbolCur(symbolName)) return symbols.get(symbolName);
        ErrorHandling.processSemanticError("c", lineNumber);
        return null;
    }

    public boolean hasSymbolCur(String symbolName) {
        return symbols.containsKey(symbolName);
    }

    public void addSymbol(Symbol symbol) {
        symbols.put(symbol.getName(), symbol);
        record.add(scopeNumber + ' ' + symbol.getName() + ' ' + symbol.getType() + '\n');
    }

    public SymbolTable getParent() {
        return parent;
    }
}
