package Frontend.SyntaxAnalysis.Nodes;

public class BlockItemNode implements Node {
    /*-- BlockItem → Decl | Stmt --*/
    private DeclNode declNode;
    private StmtNode stmtNode;

    public BlockItemNode(DeclNode declNode) {
        this.declNode = declNode;
    }

    public BlockItemNode(StmtNode stmtNode) {
        this.stmtNode = stmtNode;
    }

    @Override
    public String toString() {
        return (declNode == null ? stmtNode.toString() : declNode.toString());
    }
}
