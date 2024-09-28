package Frontend.SyntaxAnalysis.Nodes;

public class ExpNode implements Node {
    /*-- Exp → AddExp --*/
    private Node addExpNode;

    public ExpNode(AddExpNode addExpNode) {
        this.addExpNode = addExpNode;
    }
}
