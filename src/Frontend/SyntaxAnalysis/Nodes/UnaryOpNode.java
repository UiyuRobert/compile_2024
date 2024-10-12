package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

public class UnaryOpNode implements Node {
    /*-- UnaryOp → '+' | '−' | '!' --*/
    private Token opTerminal;

    public UnaryOpNode(Token opTerminal) {
        this.opTerminal = opTerminal;
    }

    public String getOp() {
        return opTerminal.getValue();
    }

    @Override
    public String toString() {
        return opTerminal.toString() + "<UnaryOp>\n";
    }
}
