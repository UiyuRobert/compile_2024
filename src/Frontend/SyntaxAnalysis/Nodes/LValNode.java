package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;
import Middle.Symbols.ConstSymbol;
import Middle.Symbols.Symbol;
import java.util.Map;
import static Middle.Visitor.curTable;

public class LValNode implements Node {
    /*-- LVal → Ident ['[' Exp ']']  --*/
    private Token identTerminal;
    private Token lbracketTerminal;
    private Node expNode;
    private Token rbracketTerminal;

    public LValNode(Token identTerminal, Token lbracketTerminal, Node expNode, Token rbracketTerminal) {
        this.identTerminal = identTerminal;
        this.lbracketTerminal = lbracketTerminal;
        this.rbracketTerminal = rbracketTerminal;
        this.expNode = expNode;
    }

    public int getValue() {
        Map.Entry<String, Integer> entry = identTerminal.getIdentifier();
        Symbol symbol = curTable.getSymbol(entry.getKey(), entry.getValue());
        if (symbol == null) return 0;
        return ((ConstSymbol)symbol).getValue();
    }

    public Map.Entry<String, Integer> getIdentifier() { return identTerminal.getIdentifier(); }

    public boolean isValInArray() { return lbracketTerminal != null; }

    public ExpNode getExp() { return (ExpNode) expNode; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(identTerminal.toString());
        if (lbracketTerminal != null) {
            sb.append(lbracketTerminal).append(expNode.toString()).append(rbracketTerminal);
        }
        sb.append("<LVal>\n");
        return sb.toString();
    }
}
