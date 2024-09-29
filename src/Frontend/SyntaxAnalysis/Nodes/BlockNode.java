package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;
import java.util.List;

public class BlockNode implements Node {
    /*-- Block → '{' { BlockItem } '}' --*/
    private Token lbraceTerminal;
    private List<Node> blockItems;
    private Token rbraceTerminal;

    public BlockNode(Token lbraceTerminal, List<Node> blockItems, Token rbraceTerminal) {
        this.lbraceTerminal = lbraceTerminal;
        this.blockItems = blockItems;
        this.rbraceTerminal = rbraceTerminal;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(lbraceTerminal.toString());
        for (Node blockItem : blockItems) {
            sb.append(blockItem.toString());
        }
        sb.append(rbraceTerminal.toString()).append("<Block>\n");
        return sb.toString();
    }
}
