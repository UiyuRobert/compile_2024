package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

public class VarDefNode implements Node {
    /*-- VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal --*/
    private Token identTerminal;
    private Token lbracketTerminal;
    private Node constExpNode;
    private Token rbracketTerminal;
    private Token assignTerminal;
    private Node initValNode;

    public VarDefNode(Token identTerminal, Token lbracketTerminal, Node constExpNode, Token rbracketTerminal) {
        this.identTerminal = identTerminal;
        this.lbracketTerminal = lbracketTerminal;
        this.constExpNode = constExpNode;
        this.rbracketTerminal = rbracketTerminal;
        this.assignTerminal = null;
        this.initValNode = null;
    }

    public VarDefNode(Token identTerminal, Token lbracketTerminal, Node constExpNode,
                      Token rbracketTerminal, Token assignTerminal, Node initValNode) {
        this.identTerminal = identTerminal;
        this.lbracketTerminal = lbracketTerminal;
        this.constExpNode = constExpNode;
        this.rbracketTerminal = rbracketTerminal;
        this.assignTerminal = assignTerminal;
        this.initValNode = initValNode;
    }
}
