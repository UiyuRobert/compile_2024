package Middle.Symbols;

import ErrorHandling.ErrorHandling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        if (table.hasSymbolCur(symbolName)) return table.getSymCurTable(symbolName);
        ErrorHandling.processSemanticError("c", lineNumber);
        return null;
    }

    public Symbol getSymCurTable(String symbolName) { return symbols.get(symbolName); }

    public boolean hasSymbolCur(String symbolName) { return symbols.containsKey(symbolName); }

    public void addSymbol(Symbol symbol) {
        symbols.put(symbol.getName(), symbol);
        record.add(this.scopeNumber + " " + symbol.getName() + ' ' + symbol.getType());
        // System.out.println(this.scopeNumber + " " + symbol.getName() + ' ' + symbol.getType());
    }

    public void fillFuncParams(String symbolName, List<Symbol> args) {
        /*-- 函数参数回填 --*/
        FuncSymbol funcSymbol = (FuncSymbol) symbols.get(symbolName);
        args.forEach(funcSymbol::addArg);
    }

    public SymbolTable getParent() {
        return parent;
    }
}
