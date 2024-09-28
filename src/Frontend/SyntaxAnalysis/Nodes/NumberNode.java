package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

public class NumberNode implements Node {
    /*-- Number → IntConst  --*/
    private Token intConstTerminal;

    public NumberNode(Token intConstTerminal) {
        this.intConstTerminal = intConstTerminal;
    }
}
