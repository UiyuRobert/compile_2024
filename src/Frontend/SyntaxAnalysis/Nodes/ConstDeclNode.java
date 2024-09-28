package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;
import java.util.List;
import java.util.Map;

public class ConstDeclNode implements Node{
    /*-- ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';' --*/
    private Token constTerminal; // 终结符 const
    private Node bType;
    private Node constDefNode;
    private List<Map.Entry<Node, Token>> constDefNodes;
    private Token semicolonTerminal;

    public ConstDeclNode(Token constTerminal, Node bType, Node constDefNode,
                         List<Map.Entry<Node, Token>> constDefNodes, Token semicolonTerminal) {
        this.constTerminal = constTerminal;
        this.bType = bType;
        this.constDefNode = constDefNode;
        this.constDefNodes = constDefNodes;
        this.semicolonTerminal = semicolonTerminal;
    }
}
