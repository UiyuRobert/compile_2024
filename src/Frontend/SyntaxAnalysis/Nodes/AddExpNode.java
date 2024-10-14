package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.KindCode;
import Frontend.LexicalAnalysis.Token;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddExpNode implements Node, Factor {
    /*-- AddExp → MulExp | AddExp ('+' | '−') MulExp --*/
    private Node mulExpNode;
    private List<Map.Entry<Node, Token>> mulExpNodes;

    public AddExpNode(Node mulExpNode, List<Map.Entry<Node, Token>> mulExpNodes) {
        this.mulExpNode = mulExpNode;
        this.mulExpNodes = mulExpNodes;
    }

    public MulExpNode getMulExp() { return (MulExpNode) mulExpNode; }

    public List<Map.Entry<MulExpNode, String>> getMulExpExps() {
        List<Map.Entry<MulExpNode, String>> mulExpExps = new ArrayList<>();
        mulExpNodes.forEach(
                e -> mulExpExps.add(new AbstractMap.SimpleEntry<>((MulExpNode) e.getKey(), e.getValue().getValue()))
        );
        return mulExpExps;
    }

    @Override
    public int getValue() {
        int ret = ((MulExpNode) mulExpNode).getValue();
        for (Map.Entry<Node, Token> entry : mulExpNodes) {
            if (entry.getValue().getKindCode() == KindCode.PLUS) ret += ((MulExpNode) entry.getKey()).getValue();
            else ret -= ((MulExpNode) entry.getKey()).getValue();
        }
        return ret;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(mulExpNode.toString());
        sb.append("<AddExp>\n");
        for(Map.Entry<Node, Token> entry : mulExpNodes){
            sb.append(entry.getValue().toString()).append(entry.getKey().toString()).append("<AddExp>\n");
        }
        return sb.toString();
    }
}
