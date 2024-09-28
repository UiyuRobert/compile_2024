package Frontend.SyntaxAnalysis.Nodes;

/*-- 声明 --*/
public class DeclNode implements Node {
    /*-- 为常量声明或变量声明 --*/
    private Node declNode;

    public DeclNode(Node declNode) {
        this.declNode = declNode;
    }
}
