package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

public class FuncDefNode implements Node{
    /*-- FuncDef → FuncType Ident '(' [FuncFParams] ')' Block --*/
    private Node funcTypeNode;
    private Token identTerminal;
    private Token lparenTerminal;
    private Node funcFParams;
    private Token rparenTerminal;
    private Node blockNode;

    public FuncDefNode(Node funcTypeNode, Token identTerminal, Token lparenTerminal,
                       Node funcFParams, Token rparenTerminal, Node blockNode) {
        this.funcTypeNode = funcTypeNode;
        this.identTerminal = identTerminal;
        this.lparenTerminal = lparenTerminal;
        this.funcFParams = funcFParams;
        this.rparenTerminal = rparenTerminal;
        this.blockNode = blockNode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(funcTypeNode.toString());
        sb.append(identTerminal.toString()).append(lparenTerminal.toString());
        if (funcFParams != null) sb.append(funcFParams);
        sb.append(rparenTerminal.toString()).append(blockNode.toString()).append("<FuncDef>\n");
        return sb.toString();
    }
}
