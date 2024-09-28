package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

import java.util.List;
import java.util.Map;

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
}
