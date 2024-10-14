package Frontend.SyntaxAnalysis;

import ErrorHandling.ErrorHandling;
import Frontend.LexicalAnalysis.KindCode;
import Frontend.LexicalAnalysis.Token;
import Frontend.LexicalAnalysis.TokenList;
import Frontend.SyntaxAnalysis.Nodes.*;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Parser {
    private List<Token> tokens;
    private Token currentToken;
    private int index;
    private Node compUnitNode;

    public Parser(TokenList tokenList) {
        this.tokens = tokenList.getTokens();
        index = 0;
        currentToken = this.tokens.get(index);
    }

    public CompUnitNode parse() {
        compUnitNode = parseCompUnit();
        return (CompUnitNode) compUnitNode;
    }

    public String getParseResult() {
        return compUnitNode.toString();
    }

    private Token match(KindCode[] kindExpected) {
        // System.out.println("now parse lineNum: " + currentToken.getLineNumber());
        Token token = currentToken;
        /*-- 对 currentToken 进行判断 --*/
        boolean isWrong = true;
        for (KindCode kind : kindExpected) {
            if (currentToken.getKindCode() == kind) { isWrong = false; break; }
        }
        if (isWrong) {
            int errLineNum = tokens.get(index-1).getLineNumber();
            return ErrorHandling.processSyntaxError(kindExpected[0], errLineNum);
        }
        ++index;
        currentToken = index >= tokens.size() ? null : tokens.get(index);
        return token;
    }

    private Token lookAhead(int num) {
        if (index >= tokens.size()) {
            return null;
        }
        return tokens.get(index + num);
    }

    /*---------------------------------------------- COMPUNIT PART ----------------------------------------------*/

    private CompUnitNode parseCompUnit() {
        /*-- CompUnit → {Decl} {FuncDef} MainFuncDef --*/
        List<Node> declNodes = new ArrayList<>();
        List<Node> funcDefNodes = new ArrayList<>();
        while (lookAhead(2).getKindCode()!= KindCode.LPARENT) {
            /*-- 解析 Decl --*/
            Node declNode = parseDecl();
            declNodes.add(declNode);
        }
        while (lookAhead(1).getKindCode()!= KindCode.MAINTK) {
            /*-- 解析 FuncDef --*/
            Node funcDefNode = parseFuncDef();
            funcDefNodes.add(funcDefNode);
        }
        Node mainFuncDefNode = parseMainFuncDef();
        return new CompUnitNode(declNodes, funcDefNodes, mainFuncDefNode);
    }

    /*---------------------------------------------- DECL PART ----------------------------------------------*/

    private DeclNode parseDecl() {
        /*-- Decl → ConstDecl | VarDecl --*/
        if (currentToken.getKindCode() == KindCode.CONSTTK) return new DeclNode(parseConstDecl(), null);
        else return new DeclNode(null, parseVarDecl());
    }

    private BTypeNode parseBType() {
        /*-- BType → 'int' | 'char' --*/
        Token bTypeTerminal = match(new KindCode[]{KindCode.INTTK, KindCode.CHARTK});
        return new BTypeNode(bTypeTerminal);
    }

    /*---------------------------------------------- DECL.CONST PART ----------------------------------------------*/

    private ConstDeclNode parseConstDecl() {
        /*-- ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';' --*/
        Token constTerminal = match(new KindCode[]{KindCode.CONSTTK});
        Node bTypeNode = parseBType();
        Node constDefNode = parseConstDef();
        List<Map.Entry<Node, Token>> constDefNodes = new ArrayList<>();
        while (currentToken.getKindCode() == KindCode.COMMA) {
            Token commaTerminal = match(new KindCode[]{KindCode.COMMA});
            Node constDefNode_ = parseConstDef();
            constDefNodes.add(new AbstractMap.SimpleImmutableEntry<>(constDefNode_, commaTerminal));
        }
        Token semicolonTerminal = match(new KindCode[]{KindCode.SEMICN});
        return new ConstDeclNode(constTerminal, bTypeNode, constDefNode,
                constDefNodes, semicolonTerminal);
    }

    private ConstDefNode parseConstDef() {
        /*-- ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal --*/
        Token identTerminal = match(new KindCode[]{KindCode.IDENFR});
        Token lbracketTerminal = null;
        Node constExpNode = null;
        Token rbracketTerminal = null;
        if (currentToken.getKindCode() == KindCode.LBRACK) {
            lbracketTerminal = match(new KindCode[]{KindCode.LBRACK});
            constExpNode = parseConstExp();
            rbracketTerminal = match(new KindCode[]{KindCode.RBRACK});
        }
        Token assignTerminal = match(new KindCode[]{KindCode.ASSIGN});
        Node constInitValNode = parseConstInitVal();
        return new ConstDefNode(identTerminal, lbracketTerminal, constExpNode, rbracketTerminal,
                assignTerminal, constInitValNode);
    }

    private ConstInitValNode parseConstInitVal() {
        /*-- ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst --*/
        if (currentToken.getKindCode() == KindCode.LBRACE) {
            /*-- '{' [ ConstExp { ',' ConstExp } ] '}' --*/
            Token lbraceTerminal = match(new KindCode[]{KindCode.LBRACE});
            Node constExpNode = null;
            List<Map.Entry<Node, Token>> constExpNodes = new ArrayList<>();
            if (currentToken.getKindCode() != KindCode.RBRACE) {
                constExpNode = parseConstExp();
                while (currentToken.getKindCode() == KindCode.COMMA) {
                    Token commaTerminal = match(new KindCode[]{KindCode.COMMA});
                    Node constExpNode_ = parseConstExp();
                    constExpNodes.add(new AbstractMap.SimpleEntry<>(constExpNode_, commaTerminal));
                }
            }
            Token rbraceTerminal = match(new KindCode[]{KindCode.RBRACE});
            return new ConstInitValNode(lbraceTerminal, constExpNode, constExpNodes, rbraceTerminal);
        } else if (currentToken.getKindCode() == KindCode.STRCON) {
            return new ConstInitValNode(match(new KindCode[]{KindCode.STRCON}));
        } else {
            return new ConstInitValNode(parseConstExp());
        }
    }

    /*---------------------------------------------- DECL.VAR PART ----------------------------------------------*/

    private VarDeclNode parseVarDecl() {
        /*-- VarDecl → BType VarDef { ',' VarDef } ';' --*/
        Node bTypeNode = parseBType();
        Node varDefNode = parseVarDef();
        List<Map.Entry<Node, Token>> varDefNodes = new ArrayList<>();
        while (currentToken.getKindCode() == KindCode.COMMA) {
            Token commaTerminal = match(new KindCode[]{KindCode.COMMA});
            Node varDefNode_ = parseVarDef();
            varDefNodes.add(new AbstractMap.SimpleImmutableEntry<>(varDefNode_, commaTerminal));
        }
        Token semicolonTerminal = match(new KindCode[]{KindCode.SEMICN});
        return new VarDeclNode(bTypeNode, varDefNode, varDefNodes, semicolonTerminal);
    }

    private VarDefNode parseVarDef() {
        /*-- VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal --*/
        Token identTerminal = match(new KindCode[]{KindCode.IDENFR});
        Token lbracketTerminal = null;
        Node constExpNode = null;
        Token rbracketTerminal = null;
        if (currentToken.getKindCode() == KindCode.LBRACK) {
            lbracketTerminal = match(new KindCode[]{KindCode.LBRACK});
            constExpNode = parseConstExp();
            rbracketTerminal = match(new KindCode[]{KindCode.RBRACK});
        }
        if (currentToken.getKindCode() == KindCode.ASSIGN) {
            Token assignTerminal = match(new KindCode[]{KindCode.ASSIGN});
            Node initValNode = parseInitVal();
            return new VarDefNode(identTerminal, lbracketTerminal, constExpNode,
                    rbracketTerminal, assignTerminal, initValNode);
        }
        return new VarDefNode(identTerminal, lbracketTerminal, constExpNode, rbracketTerminal);
    }

    private InitValNode parseInitVal() {
        /*-- InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst --*/
        if (currentToken.getKindCode() == KindCode.STRCON) {
            return new InitValNode(match(new KindCode[]{KindCode.STRCON}));
        } else if (currentToken.getKindCode() == KindCode.LBRACE) {
            Token lbraceTerminal = match(new KindCode[]{KindCode.LBRACE});
            Node expNode = null;
            List<Map.Entry<Node, Token>> expNodes = new ArrayList<>();
            if (currentToken.getKindCode() != KindCode.RBRACE) {
                expNode = parseExp();
                while (currentToken.getKindCode() == KindCode.COMMA) {
                    Token commaTerminal = match(new KindCode[]{KindCode.COMMA});
                    Node expNode_ = parseExp();
                    expNodes.add(new AbstractMap.SimpleImmutableEntry<>(expNode_, commaTerminal));
                }
            }
            Token rbraceTerminal = match(new KindCode[]{KindCode.RBRACE});
            return new InitValNode(lbraceTerminal, expNode, expNodes, rbraceTerminal);
        } else {
            return new InitValNode(parseExp());
        }
    }

    /*---------------------------------------------- FUNC PART ----------------------------------------------*/

    private FuncDefNode parseFuncDef() {
        /*-- FuncDef → FuncType Ident '(' [FuncFParams] ')' Block --*/
        Node funcTypeNode = parseFuncType();
        Token identTerminal = match(new KindCode[]{KindCode.IDENFR});
        Token lparenTerminal = match(new KindCode[]{KindCode.LPARENT});
        Node funcFParams = null;
        if (currentToken.getKindCode() != KindCode.RPARENT) {
            funcFParams = parseFuncFParams();
        }
        Token rparenTerminal = match(new KindCode[]{KindCode.RPARENT});
        Node blockNode = parseBlock();
        return new FuncDefNode(funcTypeNode, identTerminal, lparenTerminal,
                funcFParams, rparenTerminal, blockNode);
    }

    private FuncTypeNode parseFuncType() {
        /*-- FuncType → 'void' | 'int' | 'char' --*/
        return new FuncTypeNode(match(new KindCode[]{KindCode.INTTK, KindCode.CHARTK, KindCode.VOIDTK}));
    }

    private FuncFParamsNode parseFuncFParams() {
        /*-- FuncFParams → FuncFParam { ',' FuncFParam } --*/
        Node funcFParamNode = parseFuncFParam();
        List<Map.Entry<Node, Token>> funcFParamNodes = new ArrayList<>();
        while (currentToken.getKindCode() == KindCode.COMMA) {
            Token commaTerminal = match(new KindCode[]{KindCode.COMMA});
            Node funcFParamNode_ = parseFuncFParam();
            funcFParamNodes.add(new AbstractMap.SimpleImmutableEntry<>(funcFParamNode_, commaTerminal));
        }
        return new FuncFParamsNode(funcFParamNode, funcFParamNodes);
    }

    private FuncFParamNode parseFuncFParam() {
        /*-- FuncFParam → BType Ident ['[' ']'] --*/
        Node bTypeNode = parseBType();
        Token identTerminal = match(new KindCode[]{KindCode.IDENFR});
        Token lbracketTerminal = null;
        Token rbracketTerminal = null;
        if (currentToken.getKindCode() == KindCode.LBRACK) {
            lbracketTerminal = match(new KindCode[]{KindCode.LBRACK});
            rbracketTerminal = match(new KindCode[]{KindCode.RBRACK});
        }
        return new FuncFParamNode(bTypeNode, identTerminal, lbracketTerminal, rbracketTerminal);
    }

    private MainFucDefNode parseMainFuncDef() {
        /*-- MainFuncDef → 'int' 'main' '(' ')' Block --*/
        Token intTerminal = match(new KindCode[]{KindCode.INTTK});
        Token mainTerminal = match(new KindCode[]{KindCode.MAINTK});
        Token lparenTerminal = match(new KindCode[]{KindCode.LPARENT});
        Token rparenTerminal = match(new KindCode[]{KindCode.RPARENT});
        Node blockNode = parseBlock();
        return new MainFucDefNode(intTerminal, mainTerminal, lparenTerminal, rparenTerminal, blockNode);
    }

    /*---------------------------------------------- BLOCK PART ----------------------------------------------*/

    private BlockNode parseBlock() {
        /*-- Block → '{' { BlockItem } '}' --*/
        Token lbraceTerminal = match(new KindCode[]{KindCode.LBRACE});
        List<Node> blockItems = new ArrayList<>();
        while (currentToken.getKindCode() != KindCode.RBRACE) {
            Node blockItem = parseBlockItem();
            blockItems.add(blockItem);
        }
        Token rbraceTerminal = match(new KindCode[]{KindCode.RBRACE});
        return new BlockNode(lbraceTerminal, blockItems, rbraceTerminal);
    }

    private BlockItemNode parseBlockItem() {
        /*-- BlockItem → Decl | Stmt --*/
        if (isDecl(currentToken)) return new BlockItemNode(parseDecl());
        else return new BlockItemNode(parseStmt());
    }

    /*---------------------------------------------- STMT PART ----------------------------------------------*/

    private StmtNode parseStmt() {
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
        switch (currentToken.getKindCode()) {
            case IFTK: {
                /*-- 'if' '(' Cond ')' Stmt [ 'else' Stmt ] --*/
                Token ifTerminal = match(new KindCode[]{KindCode.IFTK});
                Token lparenTerminal = match(new KindCode[]{KindCode.LPARENT});
                Node condNode = parseCond();
                Token rparenTerminal = match(new KindCode[]{KindCode.RPARENT});
                Node stmtNode = parseStmt();
                Token elseTerminal = null;
                Node elseStmtNode = null;
                if (currentToken.getKindCode() == KindCode.ELSETK) {
                    elseTerminal = match(new KindCode[]{KindCode.ELSETK});
                    elseStmtNode = parseStmt();
                }
                return new StmtNode(ifTerminal, lparenTerminal, condNode, rparenTerminal,
                        stmtNode, elseTerminal, elseStmtNode);
            }
            case FORTK: {
                /*-- 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt --*/
                Token forTerminal = match(new KindCode[]{KindCode.FORTK});
                Token lparenTerminal = match(new KindCode[]{KindCode.LPARENT});
                Node forStmtNode1 = null;
                if (currentToken.getKindCode() != KindCode.SEMICN) forStmtNode1 = parseForStmt();
                Token semicolonTerminal1 = match(new KindCode[]{KindCode.SEMICN});
                Node condNode = null;
                if (currentToken.getKindCode() != KindCode.SEMICN) condNode = parseCond();
                Token semicolonTerminal2 = match(new KindCode[]{KindCode.SEMICN});
                Node forStmtNode2 = null;
                if (currentToken.getKindCode() != KindCode.RPARENT) forStmtNode2 = parseForStmt();
                Token rparenTerminal = match(new KindCode[]{KindCode.RPARENT});
                Node stmtNode = parseStmt();
                return new StmtNode(forTerminal, lparenTerminal, forStmtNode1, semicolonTerminal1,
                        condNode, semicolonTerminal2, forStmtNode2, rparenTerminal, stmtNode);
            }
            case PRINTFTK: {
                /*-- 'printf''('StringConst {','Exp}')'';' --*/
                Token printfTerminal = match(new KindCode[]{KindCode.PRINTFTK});
                Token lparenTerminal = match(new KindCode[]{KindCode.LPARENT});
                Token stringTerminal = match(new KindCode[]{KindCode.STRCON});
                List<Map.Entry<Node, Token>> expNodes = new ArrayList<>();
                while (currentToken.getKindCode() == KindCode.COMMA) {
                    Token commaTerminal = match(new KindCode[]{KindCode.COMMA});
                    Node expNode = parseExp();
                    expNodes.add(new AbstractMap.SimpleImmutableEntry<>(expNode, commaTerminal));
                }
                Token rparenTerminal = match(new KindCode[]{KindCode.RPARENT});
                Token semicolonTerminal = match(new KindCode[]{KindCode.SEMICN});
                return new StmtNode(printfTerminal, lparenTerminal, stringTerminal,
                        expNodes, rparenTerminal, semicolonTerminal);
            }
            /* -- Block -- */
            case LBRACE: return new StmtNode(parseBlock());
            /*-- 'break' ';' | 'continue' ';' --*/
            case BREAKTK:
            case CONTINUETK: {
                Token bocTerminal = match(new KindCode[]{KindCode.CONTINUETK, KindCode.BREAKTK});
                Token semicolonTerminal = match(new KindCode[]{KindCode.SEMICN});
                return new StmtNode(bocTerminal, semicolonTerminal);
            }
            case RETURNTK: {
                /*-- 'return' [Exp] ';' --*/
                Token returnTerminal = match(new KindCode[]{KindCode.RETURNTK});
                Node expNode = null;
                if (currentToken.getKindCode() != KindCode.SEMICN) expNode = parseExp();
                Token semicolonTerminal = match(new KindCode[]{KindCode.SEMICN});
                return new StmtNode(returnTerminal, expNode, semicolonTerminal);
            }
            default: {
                /*-- LVal '=' Exp ';'
                     LVal '=' 'getint''('')'';'
                     LVal '=' 'getchar''('')'';' --*/
                if (hasAssignLater()) {
                    Node lValNode = parseLVal();
                    Token assignTerminal = match(new KindCode[]{KindCode.ASSIGN});
                    if (currentToken.getKindCode() == KindCode.GETCHARTK ||
                            currentToken.getKindCode() == KindCode.GETINTTK) {
                        Token funcTerminal = match(new KindCode[]{KindCode.GETCHARTK, KindCode.GETINTTK});
                        Token lparenTerminal = match(new KindCode[]{KindCode.LPARENT});
                        Token rparenTerminal = match(new KindCode[]{KindCode.RPARENT});
                        Token semicolonTerminal = match(new KindCode[]{KindCode.SEMICN});
                        return new StmtNode(lValNode, assignTerminal, funcTerminal, lparenTerminal,
                                rparenTerminal, semicolonTerminal);
                    }
                    Node expNode = parseExp();
                    Token semicolonTerminal = match(new KindCode[]{KindCode.SEMICN});
                    return new StmtNode(lValNode, assignTerminal, expNode, semicolonTerminal);
                } else {
                    /*-- [Exp] ';' --*/
                    if (currentToken.getKindCode() == KindCode.SEMICN)
                        return new StmtNode((Node) null, match(new KindCode[]{KindCode.SEMICN}));
                    Node expNode = parseExp();
                    Token semicolonTerminal = match(new KindCode[]{KindCode.SEMICN});
                    return new StmtNode(expNode, semicolonTerminal);
                }
            }
        }
    }

    private ForStmtNode parseForStmt() {
        /*-- ForStmt → LVal '=' Exp --*/
        Node lValNode = parseLVal();
        Token assignTerminal = match(new KindCode[]{KindCode.ASSIGN});
        Node expNode = parseExp();
        return new ForStmtNode(lValNode, assignTerminal, expNode);
    }

    /*---------------------------------------------- EXP PART ----------------------------------------------*/

    private CondNode parseCond() {
        /*-- Cond → LOrExp  --*/
        return new CondNode(parseLOrExp());
    }

    private ExpNode parseExp() {
        /*-- Exp → AddExp --*/
        return new ExpNode(parseAddExp());
    }

    private AddExpNode parseAddExp() {
        /*-- AddExp → MulExp | AddExp ('+' | '−') MulExp --*/
        Node mulExpNode = parseMulExp();
        List<Map.Entry<Node, Token>> mulExpNodes = new ArrayList<>();
        while (isAddOp(currentToken)) {
            Token addOpTerminal = match(new KindCode[]{KindCode.PLUS, KindCode.MINU});
            Node mulExpNode_ = parseMulExp();
            mulExpNodes.add(new AbstractMap.SimpleEntry<>(mulExpNode_, addOpTerminal));
        }
        return new AddExpNode(mulExpNode, mulExpNodes);
    }

    private NumberNode parseNumber() {
        /*-- Number → IntConst --*/
        return new NumberNode(match(new KindCode[]{KindCode.INTCON}));
    }

    private CharacterNode parseCharacter() {
        /*-- Character → CharConst --*/
        return new CharacterNode(match(new KindCode[]{KindCode.CHRCON}));
    }

    private LValNode parseLVal() {
        /*-- LVal → Ident ['[' Exp ']'] --*/
        Token identTerminal = match(new KindCode[]{KindCode.IDENFR});
        Token lbracketTerminal = null;
        Node expNode = null;
        Token rbracketTerminal = null;
        if (currentToken.getKindCode() == KindCode.LBRACK) {
            lbracketTerminal = match(new KindCode[]{KindCode.LBRACK});
            expNode = parseExp();
            rbracketTerminal = match(new KindCode[]{KindCode.RBRACK});
        }
        return new LValNode(identTerminal, lbracketTerminal, expNode, rbracketTerminal);
    }

    private PrimaryExpNode parsePrimaryExp() {
        /*-- PrimaryExp → '(' Exp ')' | LVal | Number | Character --*/
        switch (currentToken.getKindCode()) {
            case INTCON: return new PrimaryExpNode(parseNumber());
            case CHRCON: return new PrimaryExpNode(parseCharacter());
            case IDENFR: return new PrimaryExpNode(parseLVal());
            case LPARENT: {
                Token lparenTerminal = match(new KindCode[]{KindCode.LPARENT});
                Node expNode = parseExp();
                Token rparenTerminal = match(new KindCode[]{KindCode.RPARENT});
                return new PrimaryExpNode(lparenTerminal, expNode, rparenTerminal);
            }
            default: return null;
        }
    }

    private UnaryOpNode parseUnaryOp() {
        Token unaryOpTerminal = match(new KindCode[]{KindCode.PLUS, KindCode.MINU, KindCode.NOT});
        return new UnaryOpNode(unaryOpTerminal);
    }

    private FuncRParamsNode parseFuncRParams() {
        /*-- FuncRParams → Exp { ',' Exp } --*/
        Node expNode = parseExp();
        List<Map.Entry<Node, Token>> expNodes = new ArrayList<>();
        while (currentToken.getKindCode() == KindCode.COMMA) {
            Token commaTerminal = match(new KindCode[]{KindCode.COMMA});
            Node expNode_ = parseExp();
            expNodes.add(new AbstractMap.SimpleImmutableEntry<>(expNode_, commaTerminal));
        }
        return new FuncRParamsNode(expNode, expNodes);
    }

    private UnaryExpNode parseUnaryExp() {
        /*-- UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp --*/
        if (currentToken.getKindCode() == KindCode.IDENFR && lookAhead(1)!= null
                && lookAhead(1).getKindCode() == KindCode.LPARENT) {
            /*-- Ident '(' [FuncRParams] ')' --*/
            Token identTerminal = match(new KindCode[]{KindCode.IDENFR});
            Token lparenTerminal = match(new KindCode[]{KindCode.LPARENT});
            FuncRParamsNode funcRParamsNode = null;
            if (currentToken.getKindCode() != KindCode.RPARENT) funcRParamsNode = parseFuncRParams();
            Token rparenTerminal = match(new KindCode[]{KindCode.RPARENT});
            return new UnaryExpNode(identTerminal, lparenTerminal, funcRParamsNode, rparenTerminal);
        } else if (isUnaryOp(currentToken)) {
            /*-- UnaryOp UnaryExp --*/
            Node unaryOpNode = parseUnaryOp();
            UnaryExpNode unaryExpNode = parseUnaryExp();
            return new UnaryExpNode(unaryOpNode, unaryExpNode);
        } else {
            /*-- PrimaryExp --*/
            Node primaryExpNode = parsePrimaryExp();
            return new UnaryExpNode(primaryExpNode);
        }
    }

    private MulExpNode parseMulExp() {
        /*-- MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp --*/
        Node unaryExpNode = parseUnaryExp();
        List<Map.Entry<Node, Token>> unaryNodes = new ArrayList<>();
        while (isMulOp(currentToken)) {
            Token mulTerminal = match(new KindCode[]{KindCode.MULT, KindCode.DIV, KindCode.MOD});
            Node unaryExpNode_ = parseUnaryExp();
            unaryNodes.add(new AbstractMap.SimpleImmutableEntry<>(unaryExpNode_, mulTerminal));
        }
        return new MulExpNode(unaryExpNode, unaryNodes);
    }

    private RelExpNode parseRelExp() {
        /*-- RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp --*/
        Node addExpNode = parseAddExp();
        List<Map.Entry<Node, Token>> addExpNodes = new ArrayList<>();
        while (isCompareOp(currentToken)) {
            Token compareTerminal = match(new KindCode[]{KindCode.LEQ,
                    KindCode.GEQ, KindCode.GRE, KindCode.LSS});
            Node addExpNode_ = parseAddExp();
            addExpNodes.add(new AbstractMap.SimpleImmutableEntry<>(addExpNode_, compareTerminal));
        }
        return new RelExpNode(addExpNode, addExpNodes);
    }

    private EqExpNode parseEqExp() {
        /*-- EqExp → RelExp | EqExp ('==' | '!=') RelExp --*/
        Node relExpNode = parseRelExp();
        List<Map.Entry<Node, Token>> relExpNodes = new ArrayList<>();
        while (isEqOp(currentToken)) {
            Token eqOpTerminal = match(new KindCode[]{KindCode.EQL, KindCode.NEQ});
            Node relExpNode_ = parseRelExp();
            relExpNodes.add(new AbstractMap.SimpleImmutableEntry<>(relExpNode_, eqOpTerminal));
        }
        return new EqExpNode(relExpNode, relExpNodes);
    }

    private LAndExpNode parseLAndExp() {
        /*-- LAndExp → EqExp | LAndExp '&&' EqExp --*/
        Node eqExpNode = parseEqExp();
        List<Map.Entry<Node, Token>> eqExpNodes = new ArrayList<>();
        while (currentToken.getKindCode() == KindCode.AND) {
            Token andTerminal = match(new KindCode[]{KindCode.AND});
            Node eqExpNode_ = parseEqExp();
            eqExpNodes.add(new AbstractMap.SimpleImmutableEntry<>(eqExpNode_, andTerminal));
        }
        return new LAndExpNode(eqExpNode, eqExpNodes);
    }

    private LOrExpNode parseLOrExp() {
        /*-- LOrExp → LAndExp | LOrExp '||' LAndExp --*/
        Node lAndExpNode = parseLAndExp();
        List<Map.Entry<Node, Token>> lAndExpNodes = new ArrayList<>();
        while (currentToken.getKindCode() == KindCode.OR) {
            Token orTerminal = match(new KindCode[]{KindCode.OR});
            Node lAndExpNode_ = parseLAndExp();
            lAndExpNodes.add(new AbstractMap.SimpleImmutableEntry<>(lAndExpNode_, orTerminal));
        }
        return new LOrExpNode(lAndExpNode, lAndExpNodes);
    }

    private ConstExpNode parseConstExp() {
        return new ConstExpNode(parseAddExp());
    }

    /*---------------------------------------------- ASSIST PART ----------------------------------------------*/

    private boolean isAddOp(Token token) {
        return token.getKindCode() == KindCode.PLUS || token.getKindCode() == KindCode.MINU;
    }

    public boolean isEqOp(Token token) {
        KindCode kindCode = token.getKindCode();
        return kindCode == KindCode.EQL || kindCode == KindCode.NEQ;
    }

    public boolean isCompareOp(Token token) {
        KindCode kindCode = token.getKindCode();
        return kindCode == KindCode.LSS || kindCode == KindCode.LEQ
                || kindCode == KindCode.GRE || kindCode == KindCode.GEQ;
    }

    public boolean isMulOp(Token token) {
        KindCode kindCode = token.getKindCode();
        return kindCode == KindCode.MULT || kindCode == KindCode.DIV || kindCode == KindCode.MOD;
    }

    private boolean isUnaryOp(Token token) {
        KindCode kindCode = token.getKindCode();
        return kindCode == KindCode.NOT || kindCode == KindCode.PLUS || kindCode == KindCode.MINU;
    }

    public boolean isDecl(Token token) {
        KindCode kindCode = token.getKindCode();
        return kindCode == KindCode.INTTK || kindCode == KindCode.CHARTK || kindCode == KindCode.CONSTTK;
    }

    public boolean hasAssignLater() {
        for (int i = 0;;++i) {
            if (lookAhead(i) == null || lookAhead(i).getKindCode() == KindCode.SEMICN) return false;
            else if (lookAhead(i).getKindCode() == KindCode.ASSIGN) return true;
        }
    }

}
