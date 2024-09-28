package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

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
}
