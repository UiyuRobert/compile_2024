package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;
import java.util.List;
import java.util.Map;

public class MulExpNode implements Node{
    /*-- MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp --*/
    private Node unaryNode;
    private List<Map.Entry<Node, Token>> unaryNodes;

    public MulExpNode(Node unaryNode, List<Map.Entry<Node, Token>> unaryNodes) {
        this.unaryNode = unaryNode;
        this.unaryNodes = unaryNodes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(unaryNode.toString());
        for (Map.Entry<Node, Token> entry : unaryNodes)
            sb.append(entry.getValue().toString()).append(entry.getKey().toString());
        sb.append("<MulExp>\n");
        return sb.toString();
    }
}
