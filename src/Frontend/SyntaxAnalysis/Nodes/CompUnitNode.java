package Frontend.SyntaxAnalysis.Nodes;

import java.util.List;

public class CompUnitNode implements Node {
    /*-- CompUnit → {Decl} {FuncDef} MainFuncDef --*/
    private List<Node> declNodes;
    private List<Node> funcDefNodes;
    private Node mainFuncDefNode;

    public CompUnitNode(List<Node> declNodes, List<Node> funcDefNodes, Node mainFuncDefNode) {
        this.declNodes = declNodes;
        this.funcDefNodes = funcDefNodes;
        this.mainFuncDefNode = mainFuncDefNode;
    }

    public boolean hasDeclNodes() {
        return declNodes != null && !declNodes.isEmpty();
    }

    public boolean hasFuncDefNodes() {
        return funcDefNodes != null && !funcDefNodes.isEmpty();
    }

    public List<Node> getDeclNodes() {
        return declNodes;
    }

    public List<Node> getFuncDefNodes() {
        return funcDefNodes;
    }

    public MainFucDefNode getMainFuncDefNode() {
        return (MainFucDefNode) mainFuncDefNode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Node declNode : declNodes) {
            sb.append(declNode.toString());
        }
        for (Node funcDefNode : funcDefNodes) {
            sb.append(funcDefNode.toString());
        }
        sb.append(mainFuncDefNode.toString()).append("<CompUnit>\n");
        return sb.toString();
    }
}
