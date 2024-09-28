package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;
import java.util.List;
import java.util.Map;

public class ConstInitValNode implements Node {
    /*-- ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst --*/
    private Node constExpNodeOnly;

    private Token lBraceTerminal;
    private Node constExpNode;
    private List<Map.Entry<Node, Token>> constExpNodes;
    private Token rBraceTerminal;

    private Token stringTerminal;

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
}
