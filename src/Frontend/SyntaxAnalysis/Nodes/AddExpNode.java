package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

import java.util.List;
import java.util.Map;

public class AddExpNode implements Node{
    /*-- AddExp → MulExp | AddExp ('+' | '−') MulExp --*/
    private Node mulExpNode;
    private List<Map.Entry<Node, Token>> mulExpNodes;

    public AddExpNode(Node mulExpNode, List<Map.Entry<Node, Token>> mulExpNodes) {
        this.mulExpNode = mulExpNode;
        this.mulExpNodes = mulExpNodes;
    }
}
