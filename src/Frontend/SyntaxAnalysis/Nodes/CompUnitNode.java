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
