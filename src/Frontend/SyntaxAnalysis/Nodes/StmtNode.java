package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StmtNode implements Node {
    /*-- Stmt → LVal '=' Exp ';' // i
                | [Exp] ';' // i
                | Block
                | 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // j
                | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
                | 'break' ';' | 'continue' ';' // i
                | 'return' [Exp] ';' // i
                | LVal '=' 'getint''('')'';' // i j
                | LVal '=' 'getchar''('')'';' // i j
                | 'printf''('StringConst {','Exp}')'';' / --*/

    /*-- LVal '=' Exp ';'
         LVal '=' 'getint''('')'';'
         LVal '=' 'getchar''('')''; --*/
    public enum Kind {
        IFSTMT,
        FORSTMT,
        BOCSTMT,
        RETURNSTMT,
        PRINTFSTMT,
        FUNCSTMT,
        BLOCKSTMT,
        ASSIGNSTMT,
        EXPSTMT;
    }
    private Kind kind;

    class LValStmt{
        private Node lValNode;
        private Token assignTerminal;
        private Token getFuncTerminal = null;
        private Token lparenTerminal = null;
        private Token rparenTerminal = null;
        private Node expNode_ = null;
        private Token semicolonTerminal;

        public LValStmt(Node lValNode, Token assignTerminal, Node expNode_, Token semicolonTerminal){
            this.lValNode = lValNode;
            this.assignTerminal = assignTerminal;
            this.expNode_ = expNode_;
            this.semicolonTerminal = semicolonTerminal;
        }

        public LValStmt(Node lValNode, Token assignTerminal, Token getFuncTerminal, Token lparenTerminal,
                        Token rparenTerminal, Token semicolonTerminal) {
            this.lValNode = lValNode;
            this.assignTerminal = assignTerminal;
            this.getFuncTerminal = getFuncTerminal;
            this.lparenTerminal = lparenTerminal;
            this.rparenTerminal = rparenTerminal;
            this.semicolonTerminal = semicolonTerminal;
        }

        public LValNode getLValNode(){ return (LValNode) lValNode; }

        public ExpNode getExpNode(){ return (ExpNode) expNode_; }

        public String getFuncName() {
            if (getFuncTerminal != null)
                return getFuncTerminal.getValue();
            else {
                System.out.println("FUCK ! FUNCNAME IS NULL !");
                return null;
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(lValNode.toString());
            sb.append(assignTerminal);
            if (expNode_ != null) sb.append(expNode_);
            else sb.append(getFuncTerminal.toString()).append(lparenTerminal).append(rparenTerminal);
            sb.append(semicolonTerminal).append("<Stmt>\n");
            return sb.toString();
        }
    }

    private LValStmt lValStmt = null;

    /*-- [Exp] ';' |
         'break' ';' | 'continue' ';' |
         'return' [Exp] ';' --*/
    private Node expNode = null; // 共用
    private Token breakOrContinueTerminal = null;
    private Token returnTerminal = null;
    private Token semicolonTerminal = null;

    /*-- Block --*/
    private Node blockNode = null;

    /*-- 'if' '(' Cond ')' Stmt [ 'else' Stmt ] --*/
    class IfStmt{
        private Token ifTerminal;
        private Token lparenTerminal;
        private Node conditionNode;
        private Token rparenTerminal;
        private Node stmtNode;
        private Token elseTerminal;
        private Node elseStmtNode;

        public IfStmt(Token ifTerminal, Token lparenTerminal, Node conditionNode,
                      Token rparenTerminal, Node stmtNode, Token elseTerminal, Node elseStmtNode) {
            this.ifTerminal = ifTerminal;
            this.lparenTerminal = lparenTerminal;
            this.conditionNode = conditionNode;
            this.rparenTerminal = rparenTerminal;
            this.stmtNode = stmtNode;
            this.elseTerminal = elseTerminal;
            this.elseStmtNode = elseStmtNode;
        }

        public boolean hasElseStmt(){ return elseStmtNode != null; }

        public CondNode getIfCond() { return (CondNode) conditionNode; }

        public StmtNode getIfPart() { return (StmtNode) stmtNode; }

        public StmtNode getElsePart() { return (StmtNode) elseStmtNode; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(ifTerminal.toString());
            sb.append(lparenTerminal).append(conditionNode).append(rparenTerminal).append(stmtNode);
            if (elseStmtNode != null) sb.append(elseTerminal).append(elseStmtNode);
            sb.append("<Stmt>\n");
            return sb.toString();
        }
    }
    private IfStmt ifStmt = null;

    /*-- 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt --*/
    class InForStmt {
        private Token forTerminal;
        private Token lparenTerminal;
        private Node forStmtNode1;
        private Token semicolonTerminal1;
        private Node conditionNode;
        private Token semicolonTerminal2;
        private Node forStmtNode2;
        private Token rparenTerminal;
        private Node stmtNode;

        public InForStmt(Token forTerminal, Token lparenTerminal, Node forStmtNode1,
                         Token semicolonTerminal1, Node conditionNode, Token semicolonTerminal2,
                         Node forStmtNode2, Token rparenTerminal, Node stmtNode) {
            this.forTerminal = forTerminal;
            this.lparenTerminal = lparenTerminal;
            this.forStmtNode1 = forStmtNode1;
            this.semicolonTerminal1 = semicolonTerminal1;
            this.conditionNode = conditionNode;
            this.semicolonTerminal2 = semicolonTerminal2;
            this.forStmtNode2 = forStmtNode2;
            this.rparenTerminal = rparenTerminal;
            this.stmtNode = stmtNode;
        }

        public boolean hasInFor1() { return forStmtNode1 != null; }

        public boolean hasInFor2() { return forStmtNode2 != null; }

        public boolean hasCondInFor() { return conditionNode != null; }

        public CondNode getCondInFor() { return (CondNode) conditionNode; }

        public ForStmtNode getInFor1() { return (ForStmtNode) forStmtNode1; }

        public ForStmtNode getInFor2() { return (ForStmtNode) forStmtNode2; }

        public StmtNode getStmtIn4Stmt() { return (StmtNode) stmtNode; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(forTerminal.toString());
            sb.append(lparenTerminal);
            if (forStmtNode1 != null) sb.append(forStmtNode1);
            sb.append(semicolonTerminal1);
            if (conditionNode != null) sb.append(conditionNode);
            sb.append(semicolonTerminal2);
            if (forStmtNode2 != null) sb.append(forStmtNode2);
            sb.append(rparenTerminal).append(stmtNode).append("<Stmt>\n");
            return sb.toString();
        }
    }
    private InForStmt inForStmt = null;

    /*-- 'printf''('StringConst {','Exp}')'';' --*/
    class PrintfStmt {
        private Token printfTerminal;
        private Token lparenTerminal;
        private Token stringTerminal;
        private List<Map.Entry<Node, Token>> expNodes;
        private Token rparenTerminal;
        private Token semicolonTerminal;

        public PrintfStmt(Token printfTerminal, Token lparenTerminal, Token stringTerminal,
                          List<Map.Entry<Node, Token>> expNodes, Token rparenTerminal, Token semicolonTerminal) {
            this.printfTerminal = printfTerminal;
            this.lparenTerminal = lparenTerminal;
            this.stringTerminal = stringTerminal;
            this.expNodes = expNodes;
            this.rparenTerminal = rparenTerminal;
            this.semicolonTerminal = semicolonTerminal;
        }

        public int getLineNum () { return printfTerminal.getLineNumber(); }

        public String getStringConst() {
            String str = stringTerminal.getValue()
                    .substring(1, stringTerminal.getValue().length() - 1);
            str = str.replace("\\n", "\n");
            return str;
        }

        public List<ExpNode> getExps() {
            List<ExpNode> exps = new ArrayList<>();
            expNodes.forEach(e -> exps.add((ExpNode) e.getKey()));
            return exps;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(printfTerminal.toString());
            sb.append(lparenTerminal).append(stringTerminal);
            for (Map.Entry<Node, Token> entry : expNodes)
                sb.append(entry.getValue()).append(entry.getKey());
            sb.append(rparenTerminal).append(semicolonTerminal).append("<Stmt>\n");
            return sb.toString();
        }
    }
    private PrintfStmt printfStmt = null;

    public StmtNode(Node lValNode, Token assignTerminal, Node expNode, Token semicolonTerminal){
        /*-- LVal '=' Exp ';' --*/
        this.lValStmt = new LValStmt(lValNode, assignTerminal, expNode, semicolonTerminal);
        this.kind = Kind.ASSIGNSTMT;
    }

    public ExpNode getAssignExp() { return lValStmt.getExpNode(); }

    public StmtNode(Node lValNode, Token assignTerminal, Token getFuncTerminal,
                    Token lparenTerminal, Token rparenTerminal, Token semicolonTerminal){
        /*-- LVal '=' 'getint''('')'';' |  LVal '=' 'getchar''('')''; --*/
        this.lValStmt = new LValStmt(lValNode, assignTerminal, getFuncTerminal, lparenTerminal,
                rparenTerminal, semicolonTerminal);
        this.kind = Kind.FUNCSTMT;
    }

    public LValNode getLVal() { return lValStmt.getLValNode(); }

    public String getFuncName() { return lValStmt.getFuncName(); }

    public StmtNode(Token printfTerminal, Token lparenTerminal, Token stringTerminal,
                    List<Map.Entry<Node, Token>> expNodes, Token rparenTerminal, Token semicolonTerminal) {
        /*-- 'printf''('StringConst {','Exp}')'';' --*/
        this.printfStmt = new PrintfStmt(printfTerminal, lparenTerminal, stringTerminal,
                expNodes, rparenTerminal,semicolonTerminal);
        this.kind = Kind.PRINTFSTMT;
    }

    public StmtNode(Token forTerminal, Token lparenTerminal, Node forStmtNode1,
                    Token semicolonTerminal1, Node conditionNode, Token semicolonTerminal2,
                    Node forStmtNode2, Token rparenTerminal, Node stmtNode) {
        /*-- 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt --*/
        this.inForStmt = new InForStmt(forTerminal, lparenTerminal, forStmtNode1, semicolonTerminal1,
                conditionNode, semicolonTerminal2, forStmtNode2, rparenTerminal, stmtNode);
        this.kind = Kind.FORSTMT;
    }

    public StmtNode(Token ifTerminal, Token lparenTerminal, Node conditionNode,
                    Token rparenTerminal, Node stmtNode, Token elseTerminal, Node elseStmtNode) {
        /*-- 'if' '(' Cond ')' Stmt [ 'else' Stmt ] --*/
        this.ifStmt = new IfStmt(ifTerminal, lparenTerminal, conditionNode,
                rparenTerminal, stmtNode, elseTerminal, elseStmtNode);
        this.kind = Kind.IFSTMT;
    }

    public CondNode getIfCond() { return ifStmt.getIfCond(); }

    public StmtNode(Node blockNode) {
        /*-- Block --*/
        this.blockNode = blockNode;
        this.kind = Kind.BLOCKSTMT;
    }

    public StmtNode(Node expNode, Token semicolonTerminal) {
        /*-- [Exp] ';' --*/
        this.expNode = expNode;
        this.semicolonTerminal = semicolonTerminal;
        this.kind = Kind.EXPSTMT;
    }

    public boolean hasExp() { return expNode != null; }

    public ExpNode getExp() { return (ExpNode) expNode; }

    public StmtNode(Token breakOrContinueTerminal, Token semicolonTerminal) {
        /*-- 'break' ';' | 'continue' ';' --*/
        this.breakOrContinueTerminal = breakOrContinueTerminal;
        this.semicolonTerminal = semicolonTerminal;
        this.kind = Kind.BOCSTMT;
    }

    public int getBOCLineNum() { return breakOrContinueTerminal.getLineNumber(); }

    public String getBOCStr() { return breakOrContinueTerminal.getValue(); }

    public StmtNode(Token returnTerminal, Node expNode, Token semicolonTerminal) {
        /*-- 'return' [Exp] ';' --*/
        this.returnTerminal = returnTerminal;
        this.expNode = expNode;
        this.semicolonTerminal = semicolonTerminal;
        this.kind = Kind.RETURNSTMT;
    }

    public int getRetLineNum() { return returnTerminal.getLineNumber(); }

    public Kind getKind() { return kind; }

    public BlockNode getBlock() { return (BlockNode) blockNode; }

    public StmtNode getIfStmt() { return ifStmt.getIfPart(); }

    public StmtNode getElseStmt() {
        if (ifStmt.hasElseStmt()) return ifStmt.getElsePart();
        return null;
    }

    public boolean hasInFor1() { return inForStmt.hasInFor1(); }

    public boolean hasInFor2() { return inForStmt.hasInFor2(); }

    public boolean hasCondInFor() { return inForStmt.hasCondInFor(); }

    public ForStmtNode getInFor1() { return inForStmt.getInFor1(); }

    public ForStmtNode getInFor2() { return inForStmt.getInFor2(); }

    public CondNode getCondInFor() { return inForStmt.getCondInFor(); }

    public StmtNode get4Stmt() { return inForStmt.getStmtIn4Stmt(); }

    public int getPrintLineNum() { return printfStmt.getLineNum(); }

    public String getPrintForm() { return printfStmt.getStringConst(); }

    public List<ExpNode> getPrintExp() { return printfStmt.getExps(); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch (kind) {
            case FORSTMT: return inForStmt.toString();
            case IFSTMT: return ifStmt.toString();
            case BLOCKSTMT: return blockNode.toString() + "<Stmt>\n";
            case RETURNSTMT: {
                sb.append(returnTerminal);
                if (expNode != null) sb.append(expNode);
                sb.append(semicolonTerminal).append("<Stmt>\n");
                return sb.toString();
            }
            case PRINTFSTMT: return printfStmt.toString();
            case BOCSTMT: return breakOrContinueTerminal.toString() + semicolonTerminal + "<Stmt>\n";
            case EXPSTMT: return expNode == null ? semicolonTerminal + "<Stmt>\n" :
                expNode.toString() + semicolonTerminal + "<Stmt>\n";
            default: return lValStmt.toString();
        }
    }
}
