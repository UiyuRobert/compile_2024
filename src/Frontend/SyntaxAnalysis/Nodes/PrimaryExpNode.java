package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

public class PrimaryExpNode implements Node {
    /*-- PrimaryExp → '(' Exp ')' | LVal | Number | Character --*/
    private Token lparenTerminal = null;
    private Node content;
    private Token rparenTerminal = null;

    public PrimaryExpNode(Token lparenTerminal, Node content, Token rparenTerminal) {
        this.lparenTerminal = lparenTerminal;
        this.content = content;
        this.rparenTerminal = rparenTerminal;
    }

    public PrimaryExpNode(Node content) {
        this.content = content;
    }
}
