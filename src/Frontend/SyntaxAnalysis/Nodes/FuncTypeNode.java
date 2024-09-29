package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

public class FuncTypeNode implements Node {
    /*-- FuncType → 'void' | 'int' | 'char' --*/
    private Token funcTypeTerminal;

    public FuncTypeNode(Token funcTypeTerminal) {
        this.funcTypeTerminal = funcTypeTerminal;
    }

    @Override
    public String toString() {
        return funcTypeTerminal.toString() + "<FuncType>\n";
    }
}
