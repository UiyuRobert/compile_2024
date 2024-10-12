package Middle;

import Frontend.SyntaxAnalysis.Nodes.*;
import Middle.Symbols.ConstSymbol;
import Middle.Symbols.Symbol;
import Middle.Symbols.SymbolTable;
import Middle.Symbols.VarSymbol;

import java.util.Map;

public class Visitor {
    private int domainNumber;
    public static SymbolTable curTable;

    public Visitor() {
        this.domainNumber = 0;
    }

    private boolean isRename(String name, int lineNumber) {
        boolean ret = curTable.hasSymbolCur(name);
        if (ret) {
            /* TODO */ // 错误处理
        }
        return ret;
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

    public void visit(CompUnitNode compUnitNode) {
        /*-- CompUnit → {Decl} {FuncDef} MainFuncDef --*/
        curTable = new SymbolTable(null, ++domainNumber);
        /*-- 先处理 1 层变量 --*/
        if (compUnitNode.hasDeclNodes()) {
            for (Node declNode : compUnitNode.getDeclNodes()) {
                visitDecl((DeclNode) declNode);
            }
        }
        /*-- 处理函数 --*/
        if (compUnitNode.hasFuncDefNodes()) {
            for (Node funcDefNode : compUnitNode.getFuncDefNodes()) {
                visitFuncDef((FuncDefNode) funcDefNode);
            }
        }
        visitMainFunc(compUnitNode.getMainFuncDefNode());
    }

    /*----------------------------------------------- CompUnit End ---------------------------------------------------*/

    /*----------------------------------------------- MainFunc Start -------------------------------------------------*/

    private void visitMainFunc(MainFucDefNode mainFucDefNode) {
        /*-- MainFuncDef → 'int' 'main' '(' ')' Block --*/

    }

    /*----------------------------------------------- MainFunc End ---------------------------------------------------*/

    /*----------------------------------------------- Block Start ---------------------------------------------------*/

    private void visitBlock(boolean hasReturn) {
        /*-- Block → '{' { BlockItem } '}' --*/

    }

    /*----------------------------------------------- Block End ---------------------------------------------------*/

    /*----------------------------------------------- Decl Start ---------------------------------------------------*/

    private void visitDecl(DeclNode declNode) {
        /*-- Decl → ConstDecl | VarDecl  --*/
        if (declNode.isConstDecl()) {
            visitConstDecl(declNode.getConstDeclNode());
        } else {
            visitVarDecl(declNode.getVarDeclNode());
        }
    }

    private void visitConstDecl(ConstDeclNode constDeclNode) {
        /*-- ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';' --*/
        String bType = constDeclNode.getType();
        visitConstDef(bType, constDeclNode.getConstDefNode());
        for (ConstDefNode constDefNode : constDeclNode.getConstDefNodes()) {
            visitConstDef(bType, constDefNode);
        }
    }

    private void visitConstDef(String bType, ConstDefNode constDefNode) {
        /*-- ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal --*/
        Map.Entry<String, Integer> entry = constDefNode.getIdentifier();
        String name = entry.getKey();
        int lineNumber = entry.getValue();
        boolean isArray = constDefNode.isArray();
        if (!isRename(name, lineNumber)) {
            curTable.addSymbol(new ConstSymbol(calType(bType, "Const", isArray), name, lineNumber));
        }
        /* TODO */ // 解析 ConstExp 和 ConstInitVal
    }

    private void visitVarDecl(VarDeclNode varDeclNode) {
        /*-- VarDecl → BType VarDef { ',' VarDef } ';' --*/
        String bType = varDeclNode.getType();
        visitVarDef(bType, varDeclNode.getVarDef());
        for (VarDefNode varDefNode : varDeclNode.getVarDefs()) {
            visitVarDef(bType, varDefNode);
        }
    }

    private void visitVarDef(String bType, VarDefNode varDefNode) {
        /*-- VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal --*/
        Map.Entry<String, Integer> entry = varDefNode.getIdentifier();
        String name = entry.getKey();
        int lineNumber = entry.getValue();
        boolean isArray = varDefNode.isArray();
        if (!isRename(name, lineNumber)) {
            curTable.addSymbol(new VarSymbol(calType(bType, "Var", isArray), name, lineNumber));
        }
        /* TODO */ // 解析 ConstExp 和 InitVal
    }

    /*----------------------------------------------- Decl End ---------------------------------------------------*/

    /*----------------------------------------------- Func Start ---------------------------------------------------*/

    private void visitFuncDef(FuncDefNode funcDefNode) {
        /*-- FuncDef → FuncType Ident '(' [FuncFParams] ')' Block --*/

    }

    /*----------------------------------------------- Func End ---------------------------------------------------*/

    private int visitConstExp(ConstExpNode constExpNode) {

    }

}
