package Middle;

import ErrorHandling.ErrorHandling;
import Frontend.SyntaxAnalysis.Nodes.*;
import Middle.Symbols.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Visitor {
    public static SymbolTable curTable;
    private int domainNumber;
    private int inFor;

    public Visitor() {
        this.domainNumber = 0;
        inFor = 0;
    }

    private boolean isRename(String name, int lineNumber) {
        boolean ret = curTable.hasSymbolCur(name);
        if (ret)
            /* TODO */ // 错误处理
            ErrorHandling.processSemanticError("b", lineNumber);
        return ret;
    }

    private boolean canBeUpdate(String name, int lineNumber) {
        return curTable.hasSymbol(name, lineNumber);
    }

    private Symbol.Type calType(String bType, String from, boolean isArray) {
        if (isArray) {
            if (bType.equals("Int")) {
                return from.equals("Const") ? Symbol.Type.ConstIntArray : Symbol.Type.IntArray;
            } else {
                return from.equals("Const") ? Symbol.Type.ConstCharArray : Symbol.Type.CharArray;
            }
        } else {
            if (bType.equals("Int")) {
                return from.equals("Const") ? Symbol.Type.ConstInt : Symbol.Type.Int;
            } else {
                return from.equals("Const") ? Symbol.Type.ConstChar : Symbol.Type.Char;
            }
        }
    }

    private Symbol.Type calFuncType(FuncTypeNode funcType) {
        String type = funcType.getFuncType();
        return type.equals("void") ? Symbol.Type.VoidFunc :
                type.equals("char") ? Symbol.Type.CharFunc : Symbol.Type.IntFunc;
    }

    /*----------------------------------------------- CompUnit Start ---------------------------------------------------*/

    public void visit(CompUnitNode compUnit) {
        /*-- CompUnit → {Decl} {FuncDef} MainFuncDef --*/
        curTable = new SymbolTable(null, ++domainNumber);
        /*-- 先处理 1 层变量 --*/
        if (compUnit.hasDeclNodes()) {
            for (Node declNode : compUnit.getDeclNodes()) {
                visitDecl((DeclNode) declNode);
            }
        }
        /*-- 处理函数 --*/
        if (compUnit.hasFuncDefNodes()) {
            for (Node funcDefNode : compUnit.getFuncDefNodes()) {
                visitFuncDef((FuncDefNode) funcDefNode);
            }
        }
        visitMainFunc(compUnit.getMainFuncDefNode());
    }

    /*----------------------------------------------- CompUnit End ---------------------------------------------------*/

    /*----------------------------------------------- MainFunc Start -------------------------------------------------*/

    private void visitMainFunc(MainFucDefNode mainFucDefNode) {
        /*-- MainFuncDef → 'int' 'main' '(' ')' Block --*/

    }

    /*----------------------------------------------- MainFunc End ---------------------------------------------------*/

    /*----------------------------------------------- Block Start ---------------------------------------------------*/

    private void visitNormalBlock(BlockNode block) {
        /*-- Block → '{' { BlockItem } '}' --*/

    }

    private void visitFuncBlock(BlockNode funcBlock, Symbol.Type retType) {
        /*-- Block → '{' { BlockItem } '}' --*/ // 此时已经是新的作用域了，有新的符号表

    }

    private void visitBlockItem(BlockItemNode blockItem) {
        /*-- BlockItem → Decl | Stmt --*/
        if (blockItem.isStmt()) visitStmt(blockItem.getStmt());
        else visitDecl(blockItem.getDecl());
    }

    /*----------------------------------------------- Block End ---------------------------------------------------*/

    /*----------------------------------------------- Stmt Start ---------------------------------------------------*/

    private void visitStmt(StmtNode stmt) {
        StmtNode.Kind kind = stmt.getKind();
        switch (kind) {
            case IFSTMT: visitIfStmt(stmt); break;
            case BOCSTMT: visitBOCStmt(stmt); break;
            case EXPSTMT: visitExpStmt(stmt); break;
            case FORSTMT: visitForStmt(stmt); break;
            case FUNCSTMT: visitFuncStmt(stmt); break;
            case ASSIGNSTMT: visitAssignStmt(stmt); break;
            case PRINTFSTMT: visitPrintStmt(stmt); break;
            case RETURNSTMT: visitReturnStmt(stmt); break;
            default:
                visitNormalBlock(stmt.getBlock());break;
        }
    }

    private void visitIfStmt(StmtNode ifStmt) {
        /*-- 'if' '(' Cond ')' Stmt [ 'else' Stmt ] --*/
        /* TODO */ // Cond 部分
        visitStmt(ifStmt.getIfStmt());
        if(ifStmt.getElseStmt() != null) visitStmt(ifStmt.getElseStmt());
    }

    private void visitForStmt(StmtNode forStmt) {
        /*-- 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt --*/
        ++inFor;
        if (forStmt.hasInFor1()) visitInForStmt(forStmt.getInFor1());
        if (forStmt.hasCondInFor()) visitCond(forStmt.getCondInFor());
        if (forStmt.hasInFor2()) visitInForStmt(forStmt.getInFor2());
        visitStmt(forStmt.get4Stmt());
        --inFor;
    }

    private void visitInForStmt(ForStmtNode forStmt) {
        /*-- ForStmt → LVal '=' Exp --*/
        Map.Entry<String, Integer> ident = visitLVal(forStmt.getLVal());
        visitExp(forStmt.getExp());
        if (canBeUpdate(ident.getKey(), ident.getValue())) {
            /* TODO */
        }
    }

    private void visitPrintStmt(StmtNode printStmt) {
        /*-- 'printf''('StringConst {','Exp}')'';' --*/
        List<String> forms = new ArrayList<>();
        String format = printStmt.getPrintForm();
        String regex = "%[0-9]*[a-zA-Z]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(format);
        while (matcher.find()) {
            forms.add(matcher.group());
        }
        List<ExpNode> exps = printStmt.getPrintExp();
        if (forms.size() != exps.size())
            ErrorHandling.processSemanticError("l", printStmt.getPrintLineNum());
    }

    private void visitBOCStmt(StmtNode bocStmt) {
        /*-- 'break' ';' | 'continue' ';' --*/ // 只能出现在 for 语句块中
    }

    private void visitExpStmt(StmtNode expStmt) {
        /*-- [Exp] ';' --*/
    }

    private void visitFuncStmt(StmtNode funcStmt) {
        /*-- LVal '=' 'getint''('')'';' |  LVal '=' 'getchar''('')''; --*/
    }

    private void visitAssignStmt(StmtNode assignStmt) {
        /*-- LVal '=' Exp ';' --*/
    }

    private void visitReturnStmt(StmtNode returnStmt) {

    }

    /*----------------------------------------------- Stmt End ---------------------------------------------------*/

    /*----------------------------------------------- Decl Start ---------------------------------------------------*/

    private void visitDecl(DeclNode declNode) {
        /*-- Decl → ConstDecl | VarDecl  --*/
        if (declNode.isConstDecl()) {
            visitConstDecl(declNode.getConstDeclNode());
        } else {
            visitVarDecl(declNode.getVarDeclNode());
        }
    }

    private void visitConstDecl(ConstDeclNode constDecl) {
        /*-- ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';' --*/
        String bType = constDecl.getType();
        visitConstDef(bType, constDecl.getConstDefNode());
        for (ConstDefNode constDefNode : constDecl.getConstDefNodes()) {
            visitConstDef(bType, constDefNode);
        }
    }

    private void visitConstDef(String bType, ConstDefNode constDef) {
        /*-- ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal --*/
        Map.Entry<String, Integer> entry = constDef.getIdentifier();
        String name = entry.getKey();
        int lineNumber = entry.getValue();
        boolean isArray = constDef.isArray();
        if (!isRename(name, lineNumber)) {
            curTable.addSymbol(new ConstSymbol(calType(bType, "Const", isArray), name, lineNumber));
        }
        /* TODO */ // 解析 ConstExp 和 ConstInitVal
    }

    private void visitVarDecl(VarDeclNode varDecl) {
        /*-- VarDecl → BType VarDef { ',' VarDef } ';' --*/
        String bType = varDecl.getType();
        visitVarDef(bType, varDecl.getVarDef());
        for (VarDefNode varDefNode : varDecl.getVarDefs()) {
            visitVarDef(bType, varDefNode);
        }
    }

    private void visitVarDef(String bType, VarDefNode varDef) {
        /*-- VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal --*/
        Map.Entry<String, Integer> entry = varDef.getIdentifier();
        String name = entry.getKey();
        int lineNumber = entry.getValue();
        boolean isArray = varDef.isArray();
        if (!isRename(name, lineNumber)) {
            curTable.addSymbol(new VarSymbol(calType(bType, "Var", isArray), name, lineNumber));
        }
        /* TODO */ // 解析 ConstExp 和 InitVal
    }

    /*----------------------------------------------- Decl End ---------------------------------------------------*/

    /*----------------------------------------------- Func Start ---------------------------------------------------*/

    private void visitFuncDef(FuncDefNode funcDef) {
        /*-- FuncDef → FuncType Ident '(' [FuncFParams] ')' Block --*/
        Symbol.Type type = calFuncType(funcDef.getFuncTypeNode());
        Map.Entry<String, Integer> entry = funcDef.getIdentifier();
        if (!isRename(entry.getKey(), entry.getValue())) {
            curTable.addSymbol(new FuncSymbol(type, entry.getKey(), entry.getValue()));
        }
        curTable = new SymbolTable(curTable, ++domainNumber);
        if (funcDef.hasParams()) {
            List<Symbol> params = visitFuncFParams(funcDef.getFuncFParams());
            SymbolTable outer = curTable.getParent();
            outer.fillFuncParams(entry.getKey(), params);
        }
        visitFuncBlock(funcDef.getFuncDefBlock(), type);
    }

    private List<Symbol> visitFuncFParams(FuncFParamsNode funcFParams) {
        /*-- FuncFParams → FuncFParam { ',' FuncFParam } --*/
        List<Symbol> params = new ArrayList<>();
        Symbol param = visitFuncFParam(funcFParams.getFuncFParam());
        params.add(param);
        curTable.addSymbol(param);
        for (FuncFParamNode funcFParam : funcFParams.getFuncFParams()) {
            param = visitFuncFParam(funcFParam);
            params.add(param);
            curTable.addSymbol(param);
        }
        return params;
    }

    private Symbol visitFuncFParam(FuncFParamNode funcFParam) {
        Object[] arg = funcFParam.getArg();
        Symbol.Type argType;
        if (arg[0].equals("Int") ) argType = ((boolean) arg[2] ? argType = Symbol.Type.IntArray : Symbol.Type.Int);
        else argType = ((boolean) arg[2] ? argType = Symbol.Type.CharArray : Symbol.Type.Char);
        Map.Entry<String, Integer> ident = ((Map.Entry<String, Integer>) arg[1]);
        return new Symbol(argType, ident.getKey(), ident.getValue());
    }

    /*----------------------------------------------- Func End ---------------------------------------------------*/

    private int visitConstExp(ConstExpNode constExpNode) {
        return 0;
    }

    private void visitCond(CondNode cond) {

    }

    private void visitExp(ExpNode exp) {

    }

    private Map.Entry<String, Integer> visitLVal(LValNode lVal) {
        /*-- LVal → Ident ['[' Exp ']']  --*/
        Map.Entry<String, Integer> entry = lVal.getIdentifier();
        boolean isArray = lVal.isArray();
        if (isArray) visitExp(lVal.getExp());
        return entry;
    }

}
