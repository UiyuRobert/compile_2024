package Frontend.SyntaxAnalysis.Nodes;

/*-- 声明 --*/
public class DeclNode implements Node {
    /*-- Decl → ConstDecl | VarDecl  --*/
    private Node constDeclNode = null;
    private Node varDeclNode = null;

    public DeclNode(Node constDeclNode, Node varDeclNode) {
        this.constDeclNode = constDeclNode;
        this.varDeclNode = varDeclNode;
    }

    @Override
    public String toString() {
        return constDeclNode == null ? varDeclNode.toString() : constDeclNode.toString();
    }
}
