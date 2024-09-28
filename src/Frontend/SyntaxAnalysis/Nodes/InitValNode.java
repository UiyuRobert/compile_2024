package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

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
}
