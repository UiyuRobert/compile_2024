package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InitValNode implements Node {
    /*-- InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst --*/
    private Node expNodeOnly;

    private Token lbraceTerminal;
    private Node expNode;
    private List<Map.Entry<Node, Token>> expNodes;
    private Token rbraceTerminal;

    private Token stringTerminal;

    public InitValNode(Node expNodeOnly) {
        this.expNodeOnly = expNodeOnly;
    }

    public InitValNode(Token lbraceTerminal, Node expNode,
                       List<Map.Entry<Node, Token>> expNodes, Token rbraceTerminal) {
        this.lbraceTerminal = lbraceTerminal;
        this.expNode = expNode;
        this.expNodes = expNodes;
        this.rbraceTerminal = rbraceTerminal;
    }

    public InitValNode(Token stringTerminal) {
        this.stringTerminal = stringTerminal;
    }

    public ExpNode getExpOnly() { return (ExpNode) expNodeOnly; }

    public boolean isStrInit() { return (stringTerminal != null); }

    public String getStrInit() {
        return stringTerminal.getValue().
                substring(1, stringTerminal.getValue().length() - 1);
    }

    public List<ExpNode> getExpInits() {
        List<ExpNode> exps = new ArrayList<>();
        exps.add((ExpNode) expNode);
        expNodes.forEach(e -> exps.add((ExpNode) e.getKey()));
        return exps;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (lbraceTerminal != null) {
            sb.append(lbraceTerminal);
            if (expNode != null) {
                sb.append(expNode);
                for (Map.Entry<Node, Token> entry : expNodes)
                    sb.append(entry.getValue().toString()).append(entry.getKey().toString());
            }
            sb.append(rbraceTerminal);
        } else if (expNodeOnly != null) {
            sb.append(expNodeOnly);
        } else {
            sb.append(stringTerminal.toString());
        }
        sb.append("<InitVal>\n");
        return sb.toString();
    }
}
