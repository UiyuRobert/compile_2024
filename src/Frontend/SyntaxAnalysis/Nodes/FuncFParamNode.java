package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

public class FuncFParamNode implements Node {
    /*-- FuncFParam → BType Ident ['[' ']'] --*/
    private Node bTypeNode;
    private Token identTerminal;
    private Token lbracketTerminal;
    private Token rbracketTerminal;

    public FuncFParamNode(Node bTypeNode, Token identTerminal, Token lbracketTerminal, Token rbracketTerminal) {
        this.bTypeNode = bTypeNode;
        this.identTerminal = identTerminal;
        this.lbracketTerminal = lbracketTerminal;
        this.rbracketTerminal = rbracketTerminal;
    }
}
