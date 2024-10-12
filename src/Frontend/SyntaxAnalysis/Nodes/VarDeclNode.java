package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;
import Middle.Symbols.Symbol;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VarDeclNode implements Node{
    /*-- VarDecl → BType VarDef { ',' VarDef } ';' --*/
    private Node bTypeNode;
    private Node varDefNode;
    private List<Map.Entry<Node, Token>> varDefNodes;
    private Token semicolonTerminal;

    public VarDeclNode(Node bTypeNode, Node varDefNode, List<Map.Entry<Node, Token>> varDefNodes,
                       Token semicolonTerminal){
        this.bTypeNode = bTypeNode;
        this.varDefNode = varDefNode;
        this.varDefNodes = varDefNodes;
        this.semicolonTerminal = semicolonTerminal;
    }

    public String getType() {
        return ((BTypeNode) bTypeNode).getSymbolType();
    }

    public VarDefNode getVarDef() {
        return (VarDefNode) varDefNode;
    }

    public Set<VarDefNode> getVarDefs() {
        Set<VarDefNode> varDefs = new HashSet<>();
        varDefNodes.forEach(e -> varDefs.add((VarDefNode) (e.getKey())));
        return varDefs;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(bTypeNode.toString());
        sb.append(varDefNode);
        for (Map.Entry<Node, Token> entry : varDefNodes)
            sb.append(entry.getValue()).append(entry.getKey().toString());
        sb.append(semicolonTerminal).append("<VarDecl>\n");
        return sb.toString();
    }
}
