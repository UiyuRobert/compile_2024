package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

public class UnaryExpNode implements Node {
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
