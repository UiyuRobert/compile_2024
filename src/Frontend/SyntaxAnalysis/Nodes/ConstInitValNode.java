package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;
import java.util.List;
import java.util.Map;

public class ConstInitValNode implements Node {
    /*-- ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst --*/
    private Node constExpNodeOnly = null;

    private Token lBraceTerminal = null;
    private Node constExpNode = null;
    private List<Map.Entry<Node, Token>> constExpNodes = null;
    private Token rBraceTerminal = null;

    private Token stringTerminal = null;

    public ConstInitValNode(Node constExpNodeOnly) {
        this.constExpNodeOnly = constExpNodeOnly;
    }

    public ConstInitValNode(Token lBraceTerminal, Node constExpNode,
                            List<Map.Entry<Node, Token>> constExpNodes, Token rBraceTerminal) {
        this.lBraceTerminal = lBraceTerminal;
        this.constExpNode = constExpNode;
        this.constExpNodes = constExpNodes;
        this.rBraceTerminal = rBraceTerminal;
    }

    public ConstInitValNode(Token stringTerminal) {
        this.stringTerminal = stringTerminal;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (constExpNodeOnly != null) sb.append(constExpNodeOnly);
        else if (lBraceTerminal != null) {
            sb.append(lBraceTerminal);
            if (constExpNode != null) {
                sb.append(constExpNode);
                for (Map.Entry<Node, Token> entry : constExpNodes) {
                    sb.append(entry.getValue().toString()).append(entry.getKey().toString());
                }
            }
            sb.append(rBraceTerminal);
        } else sb.append(stringTerminal.toString());
        sb.append("<ConstInitVal>\n");
        return sb.toString();
    }
}
