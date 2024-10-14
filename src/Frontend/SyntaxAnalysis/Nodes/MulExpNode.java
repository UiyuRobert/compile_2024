package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.KindCode;
import Frontend.LexicalAnalysis.Token;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MulExpNode implements Node, Factor {
    /*-- MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp --*/
    private Node unaryNode;
    private List<Map.Entry<Node, Token>> unaryNodes;

    public MulExpNode(Node unaryNode, List<Map.Entry<Node, Token>> unaryNodes) {
        this.unaryNode = unaryNode;
        this.unaryNodes = unaryNodes;
    }

    public UnaryExpNode getUnaryExp() { return (UnaryExpNode) unaryNode; }

    public List<Map.Entry<UnaryExpNode, String>> getUnaryExps() {
        List<Map.Entry<UnaryExpNode, String>> unaryExps = new ArrayList<>();
        unaryNodes.forEach(e -> unaryExps.add(
                new AbstractMap.SimpleEntry<>((UnaryExpNode) e.getKey(), e.getValue().getValue())
        ));
        return unaryExps;
    }

    @Override
    public int getValue() {
        int ret = ((UnaryExpNode) unaryNode).getValue();
        for (Map.Entry<Node, Token> entry : unaryNodes) {
            if (entry.getValue().getKindCode() == KindCode.MULT) ret *= ((UnaryExpNode) entry.getKey()).getValue();
            else if (entry.getValue().getKindCode() == KindCode.DIV) ret /= ((UnaryExpNode) entry.getKey()).getValue();
            else ret %= ((UnaryExpNode) entry.getKey()).getValue();
        }
        return ret;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(unaryNode.toString());
        sb.append("<MulExp>\n");
        for (Map.Entry<Node, Token> entry : unaryNodes)
            sb.append(entry.getValue().toString()).append(entry.getKey().toString()).append("<MulExp>\n");
        return sb.toString();
    }
}
