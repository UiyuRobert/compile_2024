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

    public LValNode getLVal() { return (LValNode) lValNode; }

    public ExpNode getExp() { return (ExpNode) expNode; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(lValNode.toString());
        sb.append(assignTerminal).append(expNode.toString()).append("<ForStmt>\n");
        return sb.toString();
    }
}
