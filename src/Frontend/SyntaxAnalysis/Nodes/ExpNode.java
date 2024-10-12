package Frontend.SyntaxAnalysis.Nodes;

public class ExpNode implements Node, Factor {
    /*-- Exp → AddExp --*/
    private Node addExpNode;

    public ExpNode(AddExpNode addExpNode) {
        this.addExpNode = addExpNode;
    }


    @Override
    public String toString() {
        return addExpNode.toString() + "<Exp>\n";
    }

    @Override
    public int getValue() {
        return ((AddExpNode) addExpNode).getValue();
    }
}
