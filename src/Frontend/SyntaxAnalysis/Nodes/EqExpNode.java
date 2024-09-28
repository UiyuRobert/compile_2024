package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;
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
}
