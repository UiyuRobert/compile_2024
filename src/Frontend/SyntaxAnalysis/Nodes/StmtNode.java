package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

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
    enum Kind {
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

    public StmtNode(Node lValNode, Token assignTerminal, Token getFuncTerminal,
                    Token lparenTerminal, Token rparenTerminal, Token semicolonTerminal){
        /*-- LVal '=' 'getint''('')'';' |  LVal '=' 'getchar''('')''; --*/
        this.lValStmt = new LValStmt(lValNode, assignTerminal, getFuncTerminal, lparenTerminal,
                rparenTerminal, semicolonTerminal);
        this.kind = Kind.FUNCSTMT;
    }

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

    public StmtNode(Token breakOrContinueTerminal, Token semicolonTerminal) {
        /*-- 'break' ';' | 'continue' ';' --*/
        this.breakOrContinueTerminal = breakOrContinueTerminal;
        this.semicolonTerminal = semicolonTerminal;
        this.kind = Kind.BOCSTMT;
    }

    public StmtNode(Token returnTerminal, Node expNode, Token semicolonTerminal) {
        /*-- 'return' [Exp] ';' --*/
        this.returnTerminal = returnTerminal;
        this.expNode = expNode;
        this.semicolonTerminal = semicolonTerminal;
        this.kind = Kind.RETURNSTMT;
    }

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
            case PRINTFSTMT: return printfStmt.toString() + "<Stmt>\n";
            case BOCSTMT: return breakOrContinueTerminal.toString() + semicolonTerminal + "<Stmt>\n";
            case EXPSTMT: return expNode == null ? semicolonTerminal + "<Stmt>\n" :
                expNode.toString() + semicolonTerminal + "<Stmt>\n";
            default: return lValStmt.toString();
        }
    }
}
