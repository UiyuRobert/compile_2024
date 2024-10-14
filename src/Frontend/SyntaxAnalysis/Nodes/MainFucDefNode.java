package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

public class MainFucDefNode implements Node{
    /*-- MainFuncDef → 'int' 'main' '(' ')' Block --*/
    private Token intTerminal;
    private Token mainTerminal;
    private Token lparenTerminal;
    private Token rparenTerminal;
    private Node blockNode;

    public MainFucDefNode(Token intTerminal, Token mainTerminal, Token lparenTerminal,
                          Token rparenTerminal, Node blockNode) {
        this.intTerminal = intTerminal;
        this.mainTerminal = mainTerminal;
        this.lparenTerminal = lparenTerminal;
        this.rparenTerminal = rparenTerminal;
        this.blockNode = blockNode;
    }

    public BlockNode getBlock() { return (BlockNode) blockNode; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(intTerminal.toString());
        sb.append(mainTerminal.toString()).append(lparenTerminal.toString()).append(rparenTerminal.toString());
        sb.append(blockNode.toString()).append("<MainFuncDef>\n");
        return sb.toString();
    }
}
