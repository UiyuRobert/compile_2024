package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

public class LValNode implements Node {
    /*-- LVal → Ident ['[' Exp ']']  --*/
    private Token identTerminal;
    private Token lbracketTerminal;
    private Node expNode;
    private Token rbracketTerminal;

    public LValNode(Token identTerminal, Token lbracketTerminal, Node expNode, Token rbracketTerminal) {
        this.identTerminal = identTerminal;
        this.lbracketTerminal = lbracketTerminal;
        this.rbracketTerminal = rbracketTerminal;
        this.expNode = expNode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(identTerminal.toString());
        if (lbracketTerminal != null) {
            sb.append(lbracketTerminal).append(expNode.toString()).append(rbracketTerminal);
        }
        sb.append("<LVal>\n");
        return sb.toString();
    }
}
