package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LAndExpNode implements Node {
    /*-- LAndExp → EqExp | LAndExp '&&' EqExp --*/
    private Node eqExpNode;
    private List<Map.Entry<Node, Token>> eqExpNodes;

    public LAndExpNode(Node eqExpNode, List<Map.Entry<Node, Token>> eqExpNodes) {
        this.eqExpNode = eqExpNode;
        this.eqExpNodes = eqExpNodes;
    }

    public List<EqExpNode> getEqExps() {
        List<EqExpNode> eqExps = new ArrayList<>();
        eqExps.add((EqExpNode) eqExpNode);
        eqExpNodes.forEach(e -> eqExps.add((EqExpNode) e.getKey()));
        return eqExps;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(eqExpNode.toString());
        sb.append("<LAndExp>\n");
        for (Map.Entry<Node, Token> entry : eqExpNodes)
            sb.append(entry.getValue().toString()).append(entry.getKey().toString()).append("<LAndExp>\n");
        return sb.toString();
    }
}
