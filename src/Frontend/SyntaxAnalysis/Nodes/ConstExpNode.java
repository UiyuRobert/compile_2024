package Frontend.SyntaxAnalysis.Nodes;

public class ConstExpNode implements Node {
    private Node addExpNode;

    public ConstExpNode(Node addExpNode) {
        this.addExpNode = addExpNode;
    }

    @Override
    public String toString() {
        return addExpNode.toString() + "<ConstExp>\n";
    }
}
