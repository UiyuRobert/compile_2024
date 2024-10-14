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

    public Object[] getArg() {
        Object[] arg = new Object[3];
        arg[0] = ((BTypeNode)bTypeNode).getSymbolType(); // 类型
        arg[1] = identTerminal.getIdentifier(); // 标识符
        arg[2] = lbracketTerminal != null; // 是不是数组类型
        return arg;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(bTypeNode.toString());
        sb.append(identTerminal.toString());
        if (lbracketTerminal != null) sb.append(lbracketTerminal).append(rbracketTerminal.toString());
        sb.append("<FuncFParam>\n");
        return sb.toString();
    }
}
