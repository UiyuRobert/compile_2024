package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

import java.util.List;
import java.util.Map;

public class FuncFParamsNode implements Node {
    /*-- FuncFParams → FuncFParam { ',' FuncFParam } --*/
    private Node funcFParam;
    private List<Map.Entry<Node, Token>> funcFParamNodes;

    public FuncFParamsNode(Node funcFParam, List<Map.Entry<Node, Token>> funcFParamNodes) {
        this.funcFParam = funcFParam;
        this.funcFParamNodes = funcFParamNodes;
    }
}
