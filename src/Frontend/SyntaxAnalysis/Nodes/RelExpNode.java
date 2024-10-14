package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

import java.util.AbstractMap;
import java.util.ArrayList;
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

    public AddExpNode getAddExp() { return (AddExpNode) addExpNode; }

    public List<Map.Entry<AddExpNode, String>> getAddExps() {
        List<Map.Entry<AddExpNode, String>> addExps = new ArrayList<>();
        addExpNodes.forEach(e ->
                addExps.add(new AbstractMap.SimpleEntry<>((AddExpNode) e.getKey(), e.getValue().getValue()))
        );
        return addExps;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(addExpNode.toString());
        sb.append("<RelExp>\n");
        for (Map.Entry<Node, Token> entry : addExpNodes) {
            sb.append(entry.getValue().toString()).append(entry.getKey().toString()).append("<RelExp>\n");
        }
        return sb.toString();
    }
}
