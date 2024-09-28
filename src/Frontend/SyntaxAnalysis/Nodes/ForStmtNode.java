package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

public class ForStmtNode implements Node {
    /*-- ForStmt → LVal '=' Exp --*/
    private Node lValNode;
    private Token assignTerminal;
    private Node expNode;

    public ForStmtNode(Node lValNode, Token assignTerminal, Node expNode) {
        this.lValNode = lValNode;
        this.assignTerminal = assignTerminal;
        this.expNode = expNode;
    }
}
