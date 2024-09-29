package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

public class ConstDefNode implements Node {
    /*-- ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal --*/
    private Token identTerminal;
    private Token lbracketTerminal;
    private Node constExpNode;
    private Token rbracketTerminal;
    private Token assignTerminal;
    private Node constInitValNode;

    public ConstDefNode(Token identTerminal, Token lbracketTerminal, Node constExpNode, Token rbracketTerminal,
                        Token assignTerminal, Node constInitValNode) {
        this.identTerminal = identTerminal;
        this.lbracketTerminal = lbracketTerminal;
        this.constExpNode = constExpNode;
        this.rbracketTerminal = rbracketTerminal;
        this.assignTerminal = assignTerminal;
        this.constInitValNode = constInitValNode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(identTerminal.toString());
        if (lbracketTerminal != null) {
            sb.append(lbracketTerminal).append(constExpNode.toString()).append(rbracketTerminal.toString());
        }
        sb.append(assignTerminal.toString()).append(constInitValNode.toString()).append("<ConstDef>\n");
        return sb.toString();
    }
}
