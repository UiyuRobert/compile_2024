package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;
import java.util.List;
import java.util.Map;

public class FuncRParamsNode implements Node {
    /*-- FuncRParams → Exp { ',' Exp } --*/
    private Node expNode;
    private List<Map.Entry<Node, Token>> expNodes; // 数量为零意味着只有一个 Exp

    public FuncRParamsNode(Node expNode, List<Map.Entry<Node, Token>> expNodes) {
        this.expNode = expNode;
        this.expNodes = expNodes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(expNode.toString());
        for (Map.Entry<Node, Token> entry : expNodes)
            sb.append(entry.getValue().toString()).append(entry.getKey().toString());
        sb.append("<FuncRParams>\n");
        return sb.toString();
    }
}
