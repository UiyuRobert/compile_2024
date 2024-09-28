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
}
