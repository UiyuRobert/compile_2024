package Middle;

import ErrorHandling.ErrorHandling;
import Frontend.LexicalAnalysis.Token;
import Frontend.SyntaxAnalysis.Nodes.*;
import Middle.LLVMIR.IRGlobalVariable;
import Middle.LLVMIR.IRModule;
import Middle.LLVMIR.IRTypes.IRArrayType;
import Middle.LLVMIR.IRTypes.IRIntType;
import Middle.LLVMIR.IRTypes.IRType;
import Middle.Symbols.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Visitor {
    public static SymbolTable curTable;
    private IRModule irModule; // 顶层模块
    private int domainNumber;
    private int inFor;

    private Symbol.Type funcEnv;
    private boolean isFinalStmt;
    private boolean hasReturn;

    public Visitor() {
        this.domainNumber = 0;
        inFor = 0;
        funcEnv = Symbol.Type.NONE;
        isFinalStmt = false;
        hasReturn = false;
        irModule = new IRModule();
    }

    private boolean isRename(String name, int lineNumber) {
        boolean ret = curTable.hasSymbolCur(name);
        if (ret)
            ErrorHandling.processSemanticError("b", lineNumber);

        return ret;
    }

    private boolean canBeUpdate(String name, int lineNumber) {
        Symbol symbol = curTable.getSymbol(name, lineNumber);
        if (symbol instanceof ConstSymbol) {
            ErrorHandling.processSemanticError("h", lineNumber);
            return false;
        }
        return true;
    }

    private Symbol.Type calType(String bType, String from, boolean isArray) {
        if (isArray) {
            if (bType.equals("Int")) {
                return from.equals("Const") ? Symbol.Type.ConstIntArray : Symbol.Type.IntArray;
            } else return from.equals("Const") ? Symbol.Type.ConstCharArray : Symbol.Type.CharArray;
        } else {
            if (bType.equals("Int")) {
                return from.equals("Const") ? Symbol.Type.ConstInt : Symbol.Type.Int;
            } else return from.equals("Const") ? Symbol.Type.ConstChar : Symbol.Type.Char;
        }
    }

    private IRType calType(String bType, int size) {
        if (size != 0) {
            if (bType.equals("Int")) return new IRArrayType(IRIntType.getI32(), size);
             else return new IRArrayType(IRIntType.getI8(), size);
        } else {
            if (bType.equals("Int")) return IRIntType.getI32();
            else return IRIntType.getI8();
        }
    }

    private Symbol.Type calFuncType(FuncTypeNode funcType) {
        String type = funcType.getFuncType();
        return type.equals("void") ? Symbol.Type.VoidFunc :
                type.equals("char") ? Symbol.Type.CharFunc : Symbol.Type.IntFunc;
    }

    /*----------------------------------------------- CompUnit Start ---------------------------------------------------*/

    public IRModule visit(CompUnitNode compUnit) {
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
        return irModule;
    }

    /*----------------------------------------------- CompUnit End ---------------------------------------------------*/

    /*----------------------------------------------- MainFunc Start -------------------------------------------------*/

    private void visitMainFunc(MainFucDefNode mainFucDefNode) {
        /*-- MainFuncDef → 'int' 'main' '(' ')' Block --*/
        curTable = new SymbolTable(curTable, ++domainNumber);
        visitFuncBlock(mainFucDefNode.getBlock(), Symbol.Type.IntFunc);

    }

    /*----------------------------------------------- MainFunc End ---------------------------------------------------*/

    /*----------------------------------------------- Block Start ---------------------------------------------------*/

    private void visitNormalBlock(BlockNode block) {
        /*-- Block → '{' { BlockItem } '}' --*/
        curTable = new SymbolTable(curTable, ++domainNumber);
        List<BlockItemNode> items = block.getBlockItems();
        for (BlockItemNode item : items)
            visitBlockItem(item);
        curTable = curTable.getParent();
    }

    private void visitFuncBlock(BlockNode funcBlock, Symbol.Type retType) {
        /*-- Block → '{' { BlockItem } '}' --*/ // 此时已经是新的作用域了，有新的符号表
        funcEnv = retType;
        List<BlockItemNode> items = funcBlock.getBlockItems();
        for (int i = 0; i < items.size(); i++) {
            if (i == items.size() - 1) isFinalStmt = true;
            visitBlockItem(items.get(i));
        }
        isFinalStmt = false;
        if (!hasReturn && (funcEnv == Symbol.Type.CharFunc || funcEnv == Symbol.Type.IntFunc))
            ErrorHandling.processSemanticError("g", funcBlock.getRbraceLineNum());
        hasReturn = false;
        funcEnv = Symbol.Type.NONE;
        curTable = curTable.getParent();
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
        isFinalStmt = false;
        visitCond(ifStmt.getIfCond());
        visitStmt(ifStmt.getIfStmt());
        if(ifStmt.getElseStmt() != null) visitStmt(ifStmt.getElseStmt());
    }

    private void visitForStmt(StmtNode forStmt) {
        /*-- 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt --*/
        isFinalStmt = false;
        ++inFor;
        if (forStmt.hasInFor1()) visitInForStmt(forStmt.getInFor1());
        if (forStmt.hasCondInFor()) visitCond(forStmt.getCondInFor());
        if (forStmt.hasInFor2()) visitInForStmt(forStmt.getInFor2());
        visitStmt(forStmt.get4Stmt());
        --inFor;
    }

    private void visitInForStmt(ForStmtNode forStmt) {
        /*-- ForStmt → LVal '=' Exp --*/
        Object[] ident = visitLVal(forStmt.getLVal());
        visitExp(forStmt.getExp());
        if (canBeUpdate((String) ident[0], (int) ident[1])) {
            /* TODO */
        }
    }

    private void visitPrintStmt(StmtNode printStmt) {
        /*-- 'printf''('StringConst {','Exp}')'';' --*/
        List<String> forms = new ArrayList<>();
        String format = printStmt.getPrintForm();
        String regex = "%[cd]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(format);
        while (matcher.find()) {
            forms.add(matcher.group());
        }
        List<ExpNode> exps = printStmt.getPrintExp();
        if (forms.size() != exps.size())
            ErrorHandling.processSemanticError("l", printStmt.getPrintLineNum());
        for (ExpNode exp : exps)
            visitExp(exp);
    }

    private void visitBOCStmt(StmtNode bocStmt) {
        /*-- 'break' ';' | 'continue' ';' --*/ // 只能出现在 for 语句块中
        if (inFor == 0) ErrorHandling.processSemanticError("m", bocStmt.getBOCLineNum());
    }

    private void visitExpStmt(StmtNode expStmt) {
        /*-- [Exp] ';' --*/
        if (expStmt.hasExp()) visitExp(expStmt.getExp());
    }

    private void visitFuncStmt(StmtNode funcStmt) {
        /*-- LVal '=' 'getint''('')'';' |  LVal '=' 'getchar''('')''; --*/
        Object[] ident = visitLVal(funcStmt.getLVal());
        if (canBeUpdate((String) ident[0], (int) ident[1])) {
            /* TODO */
        }
    }

    private void visitAssignStmt(StmtNode assignStmt) {
        /*-- LVal '=' Exp ';' --*/
        Object[] ident = visitLVal(assignStmt.getLVal());
        visitExp(assignStmt.getAssignExp());
        if (canBeUpdate((String) ident[0], (int) ident[1])) {
            /* TODO */
        }
    }

    private void visitReturnStmt(StmtNode returnStmt) {
        /*-- 'return' [Exp] ';' --*/
        if (funcEnv != Symbol.Type.NONE && isFinalStmt) hasReturn = true;
        if (returnStmt.hasExp()) {
            visitExp(returnStmt.getExp());
            if (funcEnv == Symbol.Type.VoidFunc)
                ErrorHandling.processSemanticError("f", returnStmt.getRetLineNum());
        }
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
        ConstSymbol symbol = null;
        if (!isRename(name, lineNumber)) {
            symbol = new ConstSymbol(calType(bType, "Const", isArray), name, lineNumber);
            curTable.addSymbol(symbol);
        }
        int length = 0;
        if (isArray)
            length = visitConstExp(constDef.getConstExp());
        int[] ret = visitConstInitVal(length, constDef.getConstInitVal());
        if (symbol != null) {
            if (length == 0) symbol.setInitValue(ret[0]);
            else symbol.setInitValue(ret);
        }
        if (curTable.getParent() == null) {
            // 全局作用域，生成 IRGlobalVariable
            IRGlobalVariable globalVariable = new IRGlobalVariable(calType(bType, length), name, true);
            globalVariable.setInit(ret);
            irModule.addGlobalVariable(globalVariable);
        }
    }

    private int[] visitConstInitVal(int length, ConstInitValNode constInitVal) {
        /*-- ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst --*/
        int[] ret;
        if (length == 0) {
            ret = new int[1];
            ret[0] = constInitVal.getConstOnly().getValue();
            return ret;
        }
        ret = new int[length];
        if (constInitVal.isStrVal()) {
            String str = constInitVal.getStrInit();
            for (int i = 0; i < str.length(); i++) ret[i] = str.charAt(i);
            for (int i = str.length(); i < length; i++) ret[i] = 0;
            return ret;
        }
        List<ConstExpNode> constExps = constInitVal.getConstExps();
        for (int i = 0; i < constExps.size(); i++) ret[i] = constExps.get(i).getValue();
        for (int i = constExps.size(); i < length; i++) ret[i] = 0;
        return ret;
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
        VarSymbol symbol = null;
        if (!isRename(name, lineNumber)) {
            symbol = new VarSymbol(calType(bType, "Var", isArray), name, lineNumber);
            curTable.addSymbol(symbol);
        }
        int length = 0;
        if (isArray) length = varDef.getArrayLength();
        int[] ret = null;
        if (symbol != null && varDef.hasAssign()) {
            ret = visitInitVal(length, varDef.getInitVal());
            if (ret != null) {
                if (length == 0) symbol.setInitValue(ret[0]);
                else symbol.setInitValue(ret);
            }
        }
        if (curTable.getParent() == null) {
            // 全局作用域，生成 IRGlobalVariable
            IRGlobalVariable globalVariable = new IRGlobalVariable(calType(bType, length), name, false);
            globalVariable.setInit(ret);
            irModule.addGlobalVariable(globalVariable);
        }
    }

    private int[] visitInitVal(int length, InitValNode initVal) {
        /*-- InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst --*/
        if (length == 0) {
            if (curTable.getParent() == null) {
                int[] ret = new int[1];
                ret[0] = initVal.getExpOnly().getValue();
                return ret;
            } else {
                visitExp(initVal.getExpOnly());
                return null;
            }
        }
        int[] ret = new int[length];
        if (initVal.isStrInit()) {
            String str = initVal.getStrInit();
            for (int i = 0; i < str.length(); i++) ret[i] = str.charAt(i);
            for (int i = str.length(); i < length; i++) ret[i] = 0;
            return ret;
        }
        if (curTable.getParent() == null) {
            int index = 0;
            for (ExpNode exp : initVal.getExpInits()) ret[index++] = exp.getValue();
            while (index < length) ret[index++] = 0;
            return ret;
        }
        for (ExpNode exp : initVal.getExpInits()) {
            visitExp(exp);
        }
        return null;
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
        for (FuncFParamNode funcFParam : funcFParams.getFuncFParams()) {
            Symbol param = visitFuncFParam(funcFParam);
            if (!isRename(param.getName(), param.getLineNumber())) {
                params.add(param);
                curTable.addSymbol(param);
            }
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

    private Symbol.Type visitFuncRef(Token ident, FuncRParamsNode funcRParams) {
        /*-- Ident '(' [FuncRParams] ')' --*/
        Map.Entry<String, Integer> entry = ident.getIdentifier();
        Symbol funcSym = curTable.getSymbol(entry.getKey(), entry.getValue());
        if (funcSym != null) {
            List<Symbol.Type> refs = visitFuncRParams(funcRParams);
            if (((FuncSymbol) funcSym).matchParams(refs, entry.getValue())) {
                /* TODO */
            }
            return funcSym.getType() == Symbol.Type.VoidFunc ? Symbol.Type.NONE : Symbol.Type.NotArray;
        }
        return Symbol.Type.NONE;
    }

    private List<Symbol.Type> visitFuncRParams(FuncRParamsNode funcRParams) {
        /*-- FuncRParams → Exp { ',' Exp } --*/
        List<Symbol.Type> types = new ArrayList<>();
        if (funcRParams == null) return types;
        for (ExpNode exp : funcRParams.getRParams()) {
            types.add(visitExp(exp));
        }
        return types;
    }

    /*----------------------------------------------- Func End ---------------------------------------------------*/

    /*----------------------------------------------- Exp Start ---------------------------------------------------*/

    private int visitConstExp(ConstExpNode constExp) {
        return constExp.getValue();
    }

    private void visitCond(CondNode cond) {
        /*-- Cond → LOrExp --*/
        visitLOrExp(cond.getLOrExp());
    }

    private void visitLOrExp(LOrExpNode lOrExp) {
        /*-- LOrExp → LAndExp | LOrExp '||' LAndExp --*/
        for (LAndExpNode lAndExp : lOrExp.getLAndExps()) {
            visitLAndExp(lAndExp);
        }
    }

    private void visitLAndExp(LAndExpNode lAndExp) {
        /*-- LAndExp → EqExp | LAndExp '&&' EqExp --*/
        for (EqExpNode eqExp : lAndExp.getEqExps()) {
            visitEqExp(eqExp);
        }
    }

    private void visitEqExp(EqExpNode eqExp) {
        /*-- EqExp → RelExp | EqExp ('==' | '!=') RelExp --*/
        visitRelExp(eqExp.getRelExp());
        for (Map.Entry<RelExpNode, String> entry : eqExp.getRelExps()) {
            visitRelExp(entry.getKey());
        }
    }

    private void visitRelExp(RelExpNode relExp) {
        /*-- RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp --*/
        visitAddExp(relExp.getAddExp());
        for (Map.Entry<AddExpNode, String> entry : relExp.getAddExps()) {
            visitAddExp(entry.getKey());
        }
    }

    private Symbol.Type visitExp(ExpNode exp) {
        /*-- Exp → AddExp --*/
        return visitAddExp(exp.getAddExpNode());
    }

    private Symbol.Type visitAddExp(AddExpNode addExp) {
        /*-- AddExp → MulExp | AddExp ('+' | '−') MulExp --*/
        Symbol.Type refType = visitMulExp(addExp.getMulExp());
        for (Map.Entry<MulExpNode, String> entry: addExp.getMulExpExps()) {
            visitMulExp(entry.getKey());
            /* TODO */
        }
        return refType;
    }

    private Symbol.Type visitMulExp(MulExpNode mulExp) {
        /*-- MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp --*/
        Symbol.Type refType = visitUnaryExp(mulExp.getUnaryExp());
        for (Map.Entry<UnaryExpNode, String> entry : mulExp.getUnaryExps()) {
            visitUnaryExp(entry.getKey());
            /* TODO */
        }
        return refType;
    }

    private Symbol.Type visitUnaryExp(UnaryExpNode unaryExp) {
        /*-- UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp --*/
        if (unaryExp.isPrimaryExp()) return visitPrimaryExp(unaryExp.getPrimaryExp());
        else if (unaryExp.isOpUnary()) {
            Map.Entry<UnaryExpNode, String> entry = unaryExp.getOpUnary();
            return visitUnaryExp(entry.getKey());
        }
        else {
            Map.Entry<Token, FuncRParamsNode> entry = unaryExp.getFuncRef();
            return visitFuncRef(entry.getKey(), entry.getValue());
        }
    }

    private Symbol.Type visitPrimaryExp(PrimaryExpNode primaryExp) {
        /*-- PrimaryExp → '(' Exp ')' | LVal | Number | Character --*/
        Node content = primaryExp.getContent();
        if (content instanceof ExpNode) return visitExp((ExpNode) content);
        else if (content instanceof LValNode) {
            Object[] ret = visitLVal((LValNode) content);
            Symbol symbol = curTable.getSymbol((String) ret[0], (int)ret[1]);
            if (symbol != null) return symbol.getRefType((boolean) ret[2]);
            return Symbol.Type.NONE;
        }
        else {
            // 字面量
            return Symbol.Type.NotArray;
        }
    }

    private Object[] visitLVal(LValNode lVal) {
        /*-- LVal → Ident ['[' Exp ']']  --*/
        Map.Entry<String, Integer> entry = lVal.getIdentifier();
        boolean isValInArray = lVal.isValInArray();
        if (isValInArray) visitExp(lVal.getExp());
        return new Object[]{entry.getKey(), entry.getValue(), isValInArray};
    }

}
