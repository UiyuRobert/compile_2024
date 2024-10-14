package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

public class PrimaryExpNode implements Node, Factor {
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

    public Node getContent() { return content; }

    @Override
    public int getValue() {
        if (content instanceof NumberNode) return ((NumberNode) content).getValue();
        else if (content instanceof CharacterNode) return ((CharacterNode) content).getValue();
        else if (content instanceof LValNode) return ((LValNode) content).getValue();
        else return ((ExpNode) content).getValue();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (lparenTerminal != null) {
            sb.append(lparenTerminal).append(content.toString()).append(rparenTerminal);
        } else {
            sb.append(content.toString());
        }
        sb.append("<PrimaryExp>\n");
        return sb.toString();
    }
}
