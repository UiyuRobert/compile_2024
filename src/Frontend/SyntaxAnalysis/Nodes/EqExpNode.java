package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EqExpNode implements Node {
    /*-- EqExp → RelExp | EqExp ('==' | '!=') RelExp --*/
    private Node relExpNode;
    private List<Map.Entry<Node, Token>> relExpNodes;

    public EqExpNode(Node relExpNode, List<Map.Entry<Node, Token>> relExpNodes) {
        this.relExpNode = relExpNode;
        this.relExpNodes = relExpNodes;
    }

    public RelExpNode getRelExp() { return (RelExpNode) relExpNode; }

    public List<Map.Entry<RelExpNode, String>> getRelExps() {
        List<Map.Entry<RelExpNode, String>> relExps = new ArrayList<>();
        relExpNodes.forEach(e ->
                relExps.add(new AbstractMap.SimpleEntry<>((RelExpNode) e.getKey(), e.getValue().getValue()))
        );
        return relExps;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(relExpNode.toString());
        sb.append("<EqExp>\n");
        for (Map.Entry<Node, Token> entry : relExpNodes) {
            sb.append(entry.getValue().toString()).append(entry.getKey().toString()).append("<EqExp>\n");
        }
        return sb.toString();
    }
}
