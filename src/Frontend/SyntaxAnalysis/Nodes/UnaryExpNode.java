package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

import java.util.AbstractMap;
import java.util.Map;

public class UnaryExpNode implements Node, Factor {
    /*-- UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp --*/
    private Node primaryExpNode = null;
    private Token identTerminal = null;
    private Token lparenTerminal = null;
    private Node funcRParamsNode = null;
    private Token rparenTerminal = null;
    private Node unaryOpNode = null;
    private Node unaryExpNode = null;

    public UnaryExpNode(Node primaryExpNode) {
        this.primaryExpNode = primaryExpNode;
    }

    public UnaryExpNode(Token identTerminal, Token lparenTerminal, Node funcRParamsNode, Token rparenTerminal) {
        this.identTerminal = identTerminal;
        this.lparenTerminal = lparenTerminal;
        this.funcRParamsNode = funcRParamsNode;
        this.rparenTerminal = rparenTerminal;
    }

    public UnaryExpNode(Node unaryOpNode, Node unaryExpNode) {
        this.unaryExpNode = unaryExpNode;
        this.unaryOpNode = unaryOpNode;
    }

    public boolean isPrimaryExp() { return primaryExpNode != null; }

    public boolean isFuncRef() { return identTerminal != null; }

    public boolean isOpUnary() { return unaryOpNode != null; }

    public PrimaryExpNode getPrimaryExp() { return (PrimaryExpNode) primaryExpNode; }

    public Map.Entry<Token, FuncRParamsNode> getFuncRef() {
        return new AbstractMap.SimpleEntry<>(identTerminal, (FuncRParamsNode) funcRParamsNode);
    }

    public Map.Entry<UnaryExpNode, String> getOpUnary() {
        return new AbstractMap.SimpleEntry<>((UnaryExpNode) unaryExpNode, ((UnaryOpNode)unaryOpNode).getOp());
    }

    public int getValue() {
        if (primaryExpNode != null) return ((PrimaryExpNode) primaryExpNode).getValue();
        UnaryOpNode op = (UnaryOpNode) unaryOpNode;
        UnaryExpNode exp = (UnaryExpNode) unaryExpNode;
        return op.getOp().equals("+") ? exp.getValue() : -exp.getValue();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (primaryExpNode != null) sb.append(primaryExpNode);
        else if (identTerminal != null) {
            sb.append(identTerminal).append(lparenTerminal);
            if (funcRParamsNode != null) sb.append(funcRParamsNode);
            sb.append(rparenTerminal);
        } else sb.append(unaryOpNode).append(unaryExpNode);
        sb.append("<UnaryExp>\n");
        return sb.toString();
    }
}
