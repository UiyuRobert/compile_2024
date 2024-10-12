package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;
import Middle.Symbols.Symbol;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public String getType() {
        return ((BTypeNode) bType).getSymbolType();
    }

    public ConstDefNode getConstDefNode() {
        return (ConstDefNode) constDefNode;
    }

    public Set<ConstDefNode> getConstDefNodes() {
        Set<ConstDefNode> constDefs = new HashSet<>();
        constDefNodes.forEach(e -> constDefs.add((ConstDefNode) (e.getKey())));
        return constDefs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(constTerminal.toString()).append(bType.toString()).append(constDefNode.toString());
        for(Map.Entry<Node, Token> entry : constDefNodes){
            sb.append(entry.getValue().toString()).append(entry.getKey().toString());
        }
        sb.append(semicolonTerminal.toString()).append("<ConstDecl>\n");
        return sb.toString();
    }
}
