package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

import java.util.ArrayList;
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

    public List<FuncFParamNode> getFuncFParams() {
        List<FuncFParamNode> funcFParams = new ArrayList<>();
        funcFParams.add((FuncFParamNode) funcFParam);
        funcFParamNodes.forEach(entry -> funcFParams.add((FuncFParamNode) entry.getKey()));
        return funcFParams;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(funcFParam.toString());
        for (Map.Entry<Node, Token> entry : funcFParamNodes)
            sb.append(entry.getValue().toString()).append(entry.getKey().toString());
        sb.append("<FuncFParams>\n");
        return sb.toString();
    }
}
