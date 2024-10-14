package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LOrExpNode implements Node {
    /*-- LOrExp → LAndExp | LOrExp '||' LAndExp --*/
    private Node lAndExpNode;
    private List<Map.Entry<Node, Token>> lAndExpNodes;

    public LOrExpNode(Node lAndExpNode, List<Map.Entry<Node, Token>> lAndExpNodes) {
        this.lAndExpNode = lAndExpNode;
        this.lAndExpNodes = lAndExpNodes;
    }

    public List<LAndExpNode> getLAndExps() {
        List<LAndExpNode> lAndExpExps = new ArrayList<>();
        lAndExpExps.add((LAndExpNode) lAndExpNode);
        lAndExpNodes.forEach(e -> lAndExpExps.add((LAndExpNode) e.getKey()));
        return lAndExpExps;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(lAndExpNode.toString());
        sb.append("<LOrExp>\n");
        for (Map.Entry<Node, Token> entry : lAndExpNodes)
            sb.append(entry.getValue().toString()).append(entry.getKey().toString()).append("<LOrExp>\n");
        return sb.toString();
    }
}
