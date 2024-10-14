package Frontend.SyntaxAnalysis.Nodes;

public class ConstExpNode implements Node, Factor{
    /*-- ConstExp → AddExp --*/
    private Node addExpNode;

    public ConstExpNode(Node addExpNode) {
        this.addExpNode = addExpNode;
    }

    @Override
    public int getValue() { return ((AddExpNode)addExpNode).getValue(); }

    @Override
    public String toString() {
        return addExpNode.toString() + "<ConstExp>\n";
    }
}
