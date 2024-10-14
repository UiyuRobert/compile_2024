package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

import java.util.ArrayList;
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

    public List<BlockItemNode> getBlockItems() {
        List<BlockItemNode> blockItemNodes = new ArrayList<>();
        blockItems.forEach(item -> blockItemNodes.add((BlockItemNode) item));
        return blockItemNodes;
    }

    public int getRbraceLineNum() { return rbraceTerminal.getLineNumber(); }

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
