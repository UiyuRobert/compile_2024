package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

import java.util.List;
import java.util.Map;

public class RelExpNode implements Node {
    /*-- RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp --*/
    private Node addExpNode;
    private List<Map.Entry<Node, Token>> addExpNodes;

    public RelExpNode(Node addExpNode, List<Map.Entry<Node, Token>> addExpNodes) {
        this.addExpNode = addExpNode;
        this.addExpNodes = addExpNodes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(addExpNode.toString());
        for (Map.Entry<Node, Token> entry : addExpNodes)
            sb.append(entry.getValue().toString()).append(entry.getKey().toString());
        sb.append("<RelExp>\n");
        return sb.toString();
    }
}
