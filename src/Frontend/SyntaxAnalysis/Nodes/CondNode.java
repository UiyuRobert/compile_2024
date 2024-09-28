package Frontend.SyntaxAnalysis.Nodes;

public class CondNode implements Node {
    /*-- Cond → LOrExp --*/
    private Node lOrExpNode;

    public CondNode(Node lOrExpNode) {
        this.lOrExpNode = lOrExpNode;
    }
}
