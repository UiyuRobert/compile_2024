package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

public class BTypeNode implements Node {
    /*-- BType → 'int' | 'char'  --*/
    private Token bTypeTerminal;

    public BTypeNode(Token bType) {
        this.bTypeTerminal = bType;
    }
}
