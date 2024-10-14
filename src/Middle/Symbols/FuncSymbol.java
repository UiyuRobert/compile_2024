package Middle.Symbols;

import ErrorHandling.ErrorHandling;
import Frontend.SyntaxAnalysis.Nodes.FuncRParamsNode;

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

    public boolean matchParams(List<Symbol.Type> types, int lineNumber) {
        if (types.size() != args.size()) {
            ErrorHandling.processSemanticError("d", lineNumber);
            return false;
        }
        boolean match = true;
        for (int i = 0; i < types.size(); i++) {
            if (types.get(i) != args.get(i).getRefType(false)) {
                match = false;
                ErrorHandling.processSemanticError("e", lineNumber);
                break;
            }
        }
        return match;
    }
}
