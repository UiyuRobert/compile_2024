package Middle;

import ErrorHandling.ErrorHandling;
import Frontend.LexicalAnalysis.Token;
import Frontend.SyntaxAnalysis.Nodes.*;
import Middle.LLVMIR.IRTypes.*;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.*;
import Middle.LLVMIR.IRModule;
import Middle.LLVMIR.Values.Instructions.IRBinaryInstr;
import Middle.LLVMIR.Values.Instructions.IRCall;
import Middle.LLVMIR.Values.Instructions.IRInstrType;
import Middle.LLVMIR.Values.Instructions.IRInstruction;
import Middle.LLVMIR.Values.Instructions.TypeCasting.IRTrunc;
import Middle.LLVMIR.Values.Instructions.TypeCasting.IRZext;
import Middle.LLVMIR.Values.Instructions.Memory.IRAlloca;
import Middle.LLVMIR.Values.Instructions.Memory.IRGetElePtr;
import Middle.LLVMIR.Values.Instructions.Memory.IRLoad;
import Middle.LLVMIR.Values.Instructions.Memory.IRStore;
import Middle.LLVMIR.Values.Instructions.Terminal.IRReturn;
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

    private IRFunction irFuncEnv = null; // 所在的函数环境
    private IRBasicBlock curBlock = null; // 当前处理到的基本块
    private IRValue curValue = null; // 计算表达式

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
            if (bType.equals("Int")) return new IRArrayType(IRIntType.I32(), size);
             else return new IRArrayType(IRIntType.I8(), size);
        } else {
            if (bType.equals("Int")) return IRIntType.I32();
            else return IRIntType.I8();
        }
    }

    private Symbol.Type calFuncType(FuncTypeNode funcType) {
        String type = funcType.getFuncType();
        return type.equals("void") ? Symbol.Type.VoidFunc :
                type.equals("char") ? Symbol.Type.CharFunc : Symbol.Type.IntFunc;
    }

    private IRType calIRFuncType(FuncTypeNode funcType) {
        String type = funcType.getFuncType();
        return type.equals("void") ? IRVoidType.Void() :
                type.equals("char") ? IRIntType.I8() : IRIntType.I32();
    }

    private IRValue typeTrans(IRValue toTrans, IRType target) {
        IRInstruction instr = null;
        if (target == IRIntType.I32())
            instr = new IRZext(toTrans, target);
        else if (target == IRIntType.I8())
            instr = new IRTrunc(toTrans, target);

        else {
            System.out.println("WTF ? WHICH TYPE ?");
            return null;
        }
        instr.setName("%var" + irFuncEnv.getCounter());
        curBlock.addInstruction(instr);
        return instr;
    }

    private IRValue typeCheck(IRValue toWrite, IRValue ptr) {
        IRType targetType = ((IRPtrType)ptr.getType()).getPointed();
        if (targetType == IRIntType.I8() && toWrite.getType() != targetType){
            if (toWrite instanceof IRConstant) {
                ((IRConstant) toWrite).typeCast(IRIntType.I8());
                return toWrite;
            }
            return typeTrans(toWrite, IRIntType.I8());
        }
        return toWrite;
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
        IRFuncType funcType = new IRFuncType(IRIntType.I32());
        irFuncEnv = new IRFunction(funcType, "@main");
        visitFuncBlock(mainFucDefNode.getBlock(), Symbol.Type.IntFunc);
        irModule.addFunction(irFuncEnv);
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
        curBlock = new IRBasicBlock("");
        List<BlockItemNode> items = funcBlock.getBlockItems();
        for (int i = 0; i < items.size(); i++) {
            if (i == items.size() - 1) isFinalStmt = true;
            visitBlockItem(items.get(i));
        }
        isFinalStmt = false;
        /* 返回 */
        if (!hasReturn) {
            if (funcEnv == Symbol.Type.CharFunc || funcEnv == Symbol.Type.IntFunc)
                ErrorHandling.processSemanticError("g", funcBlock.getRbraceLineNum());
            else {
                IRReturn irReturn = new IRReturn();
                curBlock.addInstruction(irReturn);
                irFuncEnv.addBlock(curBlock);
            }
        }
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
        Object[] ident = visitLVal(forStmt.getLVal(), true);
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

        String[] segments = format.split("(?=%[cd])|(?<=%[cd])");
        for (int i = 0,j = 0; i < segments.length; ++i) {
            if (segments[i].equals("%c") || segments[i].equals("%d")) {
                visitExp(exps.get(j++));
                IRValue toPrint = curValue;
                if (toPrint.getType() == IRIntType.I8())
                    toPrint = typeTrans(toPrint, IRIntType.I32());
                IRCall call = null;
                if (segments[i].equals("%c")) {
                    call = new IRCall("putchar", toPrint);
                } else
                    call = new IRCall("putint", toPrint);

                curBlock.addInstruction(call);
            } else {
                String content = segments[i] + "\\00";
                content = content.replace("\\n", "\n");
                IRArrayType arrayType = new IRArrayType(IRIntType.I8(), content.length() - 2);
                IRGlobalVariable strConst = new IRGlobalVariable(arrayType, content);
                irModule.addStrPrivate(strConst);
                ArrayList<IRValue> indexes = new ArrayList<>();
                indexes.add(new IRConstant(IRIntType.I64(), 0));
                indexes.add(new IRConstant(IRIntType.I64(), 0));
                IRGetElePtr gep = new IRGetElePtr(strConst, indexes);
                IRCall call = new IRCall("putstr", gep);
                curBlock.addInstruction(call);
            }
        }
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
        Object[] ident = visitLVal(funcStmt.getLVal(), true);
        IRValue left = curValue;
        if (canBeUpdate((String) ident[0], (int) ident[1])) {
            IRCall call = new IRCall(funcStmt.getFuncName());
            call.setName("%var" + irFuncEnv.getCounter());
            curBlock.addInstruction(call);
            IRValue toWrite = call;
            if (((IRPtrType)left.getType()).getPointed() == IRIntType.I8())
                toWrite = typeTrans(call, IRIntType.I8());
            IRStore store = new IRStore(toWrite, left);
            curBlock.addInstruction(store);
        }
    }

    private void visitAssignStmt(StmtNode assignStmt) {
        /*-- LVal '=' Exp ';' --*/
        Object[] ident = visitLVal(assignStmt.getLVal(), true);
        IRValue left = curValue;
        visitExp(assignStmt.getAssignExp());
        IRValue right = curValue;
        if (canBeUpdate((String) ident[0], (int) ident[1])) {
            right = typeCheck(right, left);
            IRStore store = new IRStore(right, left);
            store.setName("var" + irFuncEnv.getCounter());
            curBlock.addInstruction(store);
        }
    }

    private void visitReturnStmt(StmtNode returnStmt) {
        /*-- 'return' [Exp] ';' --*/
        if (funcEnv != Symbol.Type.NONE && isFinalStmt) hasReturn = true;
        if (returnStmt.hasExp()) {
            visitExp(returnStmt.getExp());
            if (funcEnv == Symbol.Type.VoidFunc)
                ErrorHandling.processSemanticError("f", returnStmt.getRetLineNum());
            IRValue retVal = curValue;
            IRType type = irFuncEnv.getReturnType();
            if (type == IRIntType.I8())
                retVal = typeTrans(retVal, IRIntType.I8());
            IRReturn ret = new IRReturn(retVal);
            curBlock.addInstruction(ret);
            if (isFinalStmt) /*TODO*/ // wrong
                irFuncEnv.addBlock(curBlock);
        } else {
            IRReturn ret = new IRReturn();
            curBlock.addInstruction(ret);
            if (isFinalStmt) /*TODO*/ // wrong
                irFuncEnv.addBlock(curBlock);
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
        Integer[] ret = visitConstInitVal(length, constDef.getConstInitVal());
        if (symbol != null) {
            if (length == 0) symbol.setInitValue(ret[0]);
            else symbol.setInitValue(ret);
        }
        if (curTable.getParent() == null) {
            IRType irType = calType(bType, length);
            // 全局作用域，生成 IRGlobalVariable
            IRGlobalVariable globalVariable = new IRGlobalVariable(irType, name, true);
            globalVariable.setInit(ret);
            globalVariable.setLength(length);
            irModule.addGlobalVariable(globalVariable);
            if (irType instanceof IRArrayType) {
                IRValue first = new IRValue(new IRPtrType(((IRArrayType) irType).getElementType()),
                        globalVariable.getName());
                symbol.setArrayFirst(first);
            }
            symbol.setIRValue(globalVariable);
        } else {
            IRType irType = calType(bType, length);
            if (irType instanceof IRArrayType) {
                IRConstArray constArray = new IRConstArray(irType, "%var" + irFuncEnv.getCounter());
                constArray.setInit(ret);
                // 如果是数组，必须将数组存到内存里
                IRAlloca alloca = new IRAlloca(irType, constArray.getName());
                curBlock.addInstruction(alloca);
                IRValue first = new IRValue(new IRPtrType(((IRArrayType) irType).getElementType()),
                        constArray.getName()); // 数组首元素的地址

                symbol.setIRValue(alloca); // 指向数组的指针
                symbol.setArrayFirst(first); // 指向数组首元素的指针

                for (int i = 0; i < constArray.size(); ++i) {
                    ArrayList<IRValue> index = new ArrayList<>();
                    index.add(new IRConstant(IRIntType.I32(), 0));
                    index.add(new IRConstant(IRIntType.I32(), i));
                    IRGetElePtr gep = new IRGetElePtr(alloca, index); // 生成写入的地址
                    gep.setName("%var" + irFuncEnv.getCounter());
                    curBlock.addInstruction(gep);

                    IRValue toWrite = constArray.getValByIndex(i); // 生成写入的数据
                    toWrite = typeCheck(toWrite, gep);
                    IRStore store = new IRStore(toWrite, gep); // store
                    curBlock.addInstruction(store);
                }
            } else {
                IRConstant constant = new IRConstant(irType, ret[0]);
                if (symbol != null)
                    symbol.setIRValue(constant);
            }
        }
    }

    private Integer[] visitConstInitVal(int length, ConstInitValNode constInitVal) {
        /*-- ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst --*/
        Integer[] ret;
        if (length == 0) {
            ret = new Integer[1];
            ret[0] = constInitVal.getConstOnly().getValue();
            return ret;
        }
        ret = new Integer[length];
        if (constInitVal.isStrVal()) {
            String str = constInitVal.getStrInit();
            for (int i = 0; i < str.length(); i++) ret[i] = (int) str.charAt(i);
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
        Object[] ret = null;
        if (symbol != null && varDef.hasAssign()) {
            ret = visitInitVal(length, varDef.getInitVal());
            if (ret instanceof Integer[]) {
                if (length == 0) symbol.setInitValue((Integer) ret[0]);
                else symbol.setInitValue((Integer[]) ret);
            }
        }

        IRType irType = calType(bType, length);
        if (curTable.getParent() == null) {
            // 全局作用域，生成 IRGlobalVariable
            IRGlobalVariable globalVariable = new IRGlobalVariable(irType, name, false);
            if (ret instanceof Integer[])
                globalVariable.setInit((Integer[]) ret);

            globalVariable.setLength(length);
            irModule.addGlobalVariable(globalVariable);
            if (irType instanceof IRArrayType) {
                IRValue first = new IRValue(new IRPtrType(((IRArrayType) irType).getElementType()),
                        globalVariable.getName());
                symbol.setArrayFirst(first);
            }
            symbol.setIRValue(globalVariable);
        } else {
            if (irType instanceof IRArrayType) {
                IRAlloca alloca = new IRAlloca(irType, "%var" + irFuncEnv.getCounter());
                curBlock.addInstruction(alloca);
                IRValue first = new IRValue(new IRPtrType(((IRArrayType) irType).getElementType()),
                        alloca.getName());
                symbol.setIRValue(alloca);
                symbol.setArrayFirst(first);
                if (ret != null) { // 有初始值
                    for (int i = 0; i < ret.length; ++i) {
                        ArrayList<IRValue> indexes = new ArrayList<>();
                        indexes.add(new IRConstant(IRIntType.I32(), 0));
                        indexes.add(new IRConstant(IRIntType.I32(), i));
                        IRGetElePtr gep = new IRGetElePtr(alloca, indexes);
                        gep.setName("%var" + irFuncEnv.getCounter());
                        curBlock.addInstruction(gep);
                        IRValue toWrite = typeCheck((IRValue) ret[i], gep);
                        IRStore store = new IRStore(toWrite, gep);
                        curBlock.addInstruction(store);
                    }
                }
            } else {
                IRAlloca alloca = new IRAlloca(irType, "%var" + irFuncEnv.getCounter());
                curBlock.addInstruction(alloca);
                symbol.setIRValue(alloca);
                if (ret != null) {
                    IRValue toWrite = typeCheck((IRValue) ret[0], alloca);
                    IRStore store = new IRStore(toWrite, alloca);
                    curBlock.addInstruction(store);
                }
            }
        }
    }

    private Object[] visitInitVal(int length, InitValNode initVal) {
        /*-- InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst --*/
        if (length == 0) {
            if (curTable.getParent() == null) {
                Integer[] ret = new Integer[1];
                ret[0] = initVal.getExpOnly().getValue();
                return ret;
            } else {
                IRValue[] ret = new IRValue[1];
                visitExp(initVal.getExpOnly());
                ret[0] = curValue;
                return ret;
            }
        }

        if (curTable.getParent() == null) {
            Integer[] ret = new Integer[length];
            if (initVal.isStrInit()) {
                String str = initVal.getStrInit();
                for (int i = 0; i < str.length(); i++) ret[i] = (int) str.charAt(i);
                for (int i = str.length(); i < length; i++) ret[i] = 0;
                return ret;
            }
            int index = 0;
            for (ExpNode exp : initVal.getExpInits()) ret[index++] = exp.getValue();
            while (index < length) ret[index++] = 0;
            return ret;
        } else {
            IRValue[] rets = new IRValue[length];
            if (initVal.isStrInit()) {
                String str = initVal.getStrInit();
                for (int i = 0; i < str.length(); i++) rets[i] = new IRConstant(IRIntType.I32(), str.charAt(i));
                for (int i = str.length(); i < length; i++) rets[i] = new IRConstant(IRIntType.I32(), 0);
                return rets;
            }
            int index = 0;
            for (ExpNode exp : initVal.getExpInits()) {
                visitExp(exp);
                rets[index++] = curValue;
            }
            return rets;
        }
    }

    /*----------------------------------------------- Decl End ---------------------------------------------------*/

    /*----------------------------------------------- Func Start ---------------------------------------------------*/

    private IRType symbolTy2IRTy(Symbol.Type type) {
        switch (type) {
            case Char: return IRIntType.I8();
            case Int: return IRIntType.I32();
            case CharArray: return new IRPtrType(IRIntType.I8());
            case IntArray: return new IRPtrType(IRIntType.I32());
            default:
                System.out.println("FUCK ! NOT HERE\n");
                return null;
        }
    }

    private void visitFuncDef(FuncDefNode funcDef) {
        /*-- FuncDef → FuncType Ident '(' [FuncFParams] ')' Block --*/
        Symbol.Type type = calFuncType(funcDef.getFuncTypeNode());
        Map.Entry<String, Integer> entry = funcDef.getIdentifier();
        FuncSymbol f = null;
        if (!isRename(entry.getKey(), entry.getValue())) {
            f = new FuncSymbol(type, entry.getKey(), entry.getValue());
            curTable.addSymbol(f);
        }
        curTable = new SymbolTable(curTable, ++domainNumber);
        irFuncEnv = new IRFunction(IRVoidType.Void(), "@" + entry.getKey());
        IRFuncType funcType = new IRFuncType(calIRFuncType(funcDef.getFuncTypeNode()));
        if (funcDef.hasParams()) {
            List<Symbol> params = visitFuncFParams(funcDef.getFuncFParams());
            /* 添加参数 */
            for (Symbol param : params) {
                // 普通类型传值，数组类型传指针
                IRType irType = symbolTy2IRTy(param.getType()); // 参数的IR类型
                IRValue p = new IRValue(irType, "%var" + irFuncEnv.getCounter()); // 参数IR
                p.setParam(true);
                funcType.addParam(p);
                param.setIRValue(p);
            }
            SymbolTable outer = curTable.getParent();
            outer.fillFuncParams(entry.getKey(), params);
        }
        // IR
        irFuncEnv.setType(funcType);
        irModule.addFunction(irFuncEnv);
        f.setIRValue(irFuncEnv);
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
            Object[] ret = visitFuncRParams(funcRParams);
            List<Symbol.Type> refs = (List<Symbol.Type>) ret[0];
            ArrayList<IRValue> args = (ArrayList<IRValue>) ret[1];
            if (((FuncSymbol) funcSym).matchParams(refs, entry.getValue())) {
                IRCall call = getIrCall(funcSym, args);
                curBlock.addInstruction(call);
                curValue = call;
            }
            return funcSym.getType() == Symbol.Type.VoidFunc ? Symbol.Type.NONE : Symbol.Type.NotArray;
        }
        return Symbol.Type.NONE;
    }

    private IRCall getIrCall(Symbol funcSym, ArrayList<IRValue> args) {
        IRFunction func = (IRFunction) funcSym.getIRValue();
        IRFuncType irFuncType = (IRFuncType) func.getType();
        ArrayList<IRValue> formalArgs = irFuncType.getParameters();
        /* 参数类型检查 */
        for (int i = 0; i < formalArgs.size(); i++) {
            if (formalArgs.get(i).getType() == IRIntType.I8() && args.get(i).getType() != IRIntType.I8())
                args.set(i, typeTrans(args.get(i), IRIntType.I8()));
        }
        IRCall call = new IRCall(func, args);
        if (func.getReturnType() != IRVoidType.Void())
            call.setName("%val" + irFuncEnv.getCounter());
        return call;
    }

    /**
     * Object[0] -> List<Symbol.Type>
     * Object[0] -> ArrayList<IRValue> 实参集合
     * */
    private Object[] visitFuncRParams(FuncRParamsNode funcRParams) {
        /*-- FuncRParams → Exp { ',' Exp } --*/
        Object[] ret = new Object[2];
        List<Symbol.Type> types = new ArrayList<>();
        ArrayList<IRValue> args = new ArrayList<>();
        ret[0] = types;
        ret[1] = args;
        if (funcRParams == null) return ret;
        for (ExpNode exp : funcRParams.getRParams()) {
            types.add(visitExp(exp));
            args.add(curValue);
        }
        return ret;
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
        IRValue left = curValue;
        for (Map.Entry<MulExpNode, String> entry: addExp.getMulExpExps()) {
            visitMulExp(entry.getKey());
            IRBinaryInstr addInstr = null;
            IRValue right = curValue;
            if (entry.getValue().equals("+"))
                addInstr = new IRBinaryInstr(IRInstrType.Add, IRIntType.I32(), left, right);
            else if (entry.getValue().equals("-"))
                addInstr = new IRBinaryInstr(IRInstrType.Sub, IRIntType.I32(), left, right);
            else System.out.println("FUCK ! NOT ADD OR SUB");
            String name = "%var" + irFuncEnv.getCounter();
            addInstr.setName(name);
            curBlock.addInstruction(addInstr);
            left = addInstr;
        }
        curValue = left;
        return refType;
    }

    private Symbol.Type visitMulExp(MulExpNode mulExp) {
        /*-- MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp --*/
        Symbol.Type refType = visitUnaryExp(mulExp.getUnaryExp());
        IRValue left = curValue;
        for (Map.Entry<UnaryExpNode, String> entry : mulExp.getUnaryExps()) {
            visitUnaryExp(entry.getKey());
            IRValue right = curValue;
            IRBinaryInstr mulInstr = null;
            if (entry.getValue().equals("*"))
                mulInstr = new IRBinaryInstr(IRInstrType.Mul, IRIntType.I32(), left, right);
            else if (entry.getValue().equals("/"))
                mulInstr = new IRBinaryInstr(IRInstrType.Sdiv, IRIntType.I32(), left, right);
            else if (entry.getValue().equals("%"))
                mulInstr = new IRBinaryInstr(IRInstrType.Srem, IRIntType.I32(), left, right);
            else System.out.println("FUCK ! NOT * / %");
            String name = "%var" + irFuncEnv.getCounter();
            mulInstr.setName(name);
            curBlock.addInstruction(mulInstr);
            left = mulInstr;
        }
        curValue = left;
        return refType;
    }

    private Symbol.Type visitUnaryExp(UnaryExpNode unaryExp) {
        /*-- UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp --*/
        if (unaryExp.isPrimaryExp()) return visitPrimaryExp(unaryExp.getPrimaryExp());
        else if (unaryExp.isOpUnary()) {
            Map.Entry<UnaryExpNode, String> entry = unaryExp.getOpUnary();
            Symbol.Type type = visitUnaryExp(entry.getKey());
            IRValue left = new IRConstant(IRIntType.I32(), 0);
            IRValue right = curValue;
            if (entry.getValue().equals("-")) {
                IRBinaryInstr addInstr = new IRBinaryInstr(IRInstrType.Sub, IRIntType.I32(), left, right);
                String name = "%var" + irFuncEnv.getCounter();
                addInstr.setName(name);
                curBlock.addInstruction(addInstr);
                curValue = addInstr;
            } else if (entry.getValue().equals("!")) {
                /* TODO */
            }
            return type;
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
            Object[] ret = visitLVal((LValNode) content, false);
            Symbol symbol = curTable.getSymbol((String) ret[0], (int)ret[1]);
            if (symbol != null) return symbol.getRefType((boolean) ret[2]);
            return Symbol.Type.NONE;
        }
        else {
            // 字面量
            if (content instanceof NumberNode) {
                int val = ((NumberNode) content).getValue();
                curValue = new IRConstant(IRIntType.I32(), val);
            } else {
                int val = ((CharacterNode) content).getValue();
                curValue = new IRConstant(IRIntType.I32(), val);
            }
            return Symbol.Type.NotArray;
        }
    }

    private Object[] visitLVal(LValNode lVal, boolean isLeft) {
        /*-- LVal → Ident ['[' Exp ']']  --*/
        Map.Entry<String, Integer> entry = lVal.getIdentifier();
        String symbolName = entry.getKey();
        Symbol symbol = curTable.getSymbol(symbolName, entry.getValue());
        boolean isValInArray = lVal.isValInArray();
        if (isValInArray) {
            visitExp(lVal.getExp());
            IRValue index = curValue;
            ArrayList<IRValue> ind = new ArrayList<>();
            ind.add(index);
            processLValArray2IR(symbol, ind, isLeft);
        } else {
            processLValSingle2IR(symbol, isLeft);
        }
        return new Object[]{entry.getKey(), entry.getValue(), isValInArray};
    }

    /**
     * 处理 lVal 不是数组的情况
     * symbol -> 标识符的符号
     * */
    private void processLValSingle2IR(Symbol symbol, boolean isLeft) {
        IRValue val = symbol.getIRValue();
        if (val.isParam()) { // 对于函数的参数
            // val 就是数组首地址的指针，没有 first
            IRType type = val.getType();
            if (!val.isAlloc()) {
                IRAlloca alloca = new IRAlloca(type, "%var" + irFuncEnv.getCounter());
                curBlock.addInstruction(alloca);
                IRStore store = new IRStore(val, alloca);
                curBlock.addInstruction(store);
                IRLoad load = null;
                load = new IRLoad(type, alloca);
                load.setName("%var" + irFuncEnv.getCounter());
                curBlock.addInstruction(load);
                load.setAlloc(true);
                load.setParam(true);
                symbol.setIRValue(alloca);
                if (isLeft) {
                    curValue = alloca;
                } else {
                    curValue = load;
                    if (type == IRIntType.I8())
                        curValue = typeTrans(load, IRIntType.I32());
                }
            } else {
                if (isLeft) {
                    curValue = val; // alloc
                } else {
                    type = ((IRPtrType)val.getType()).getPointed();
                    IRLoad load = new IRLoad(type, val);
                    load.setName("%var" + irFuncEnv.getCounter());
                    curBlock.addInstruction(load);
                    curValue = load;
                    if (type == IRIntType.I8())
                        curValue = typeTrans(val, IRIntType.I32());
                }
            }
        } else {
            // 非数组、非参数
            // 常量
            boolean isArray = symbol.isArray();
            if (isArray) {
                ArrayList<IRValue> ind = new ArrayList<>();
                ind.add(new IRConstant(IRIntType.I32(), 0));
                ind.add(new IRConstant(IRIntType.I32(), 0));
                IRGetElePtr gep = new IRGetElePtr(val, ind);
                gep.setName("%var" + irFuncEnv.getCounter());
                curBlock.addInstruction(gep);
                curValue = gep;
                return;
            }
            if (val instanceof IRConstant || isLeft)
                curValue = val;
            else {
                // 是普通变量，需要 load
                IRType type = ((IRPtrType)val.getType()).getPointed();
                IRLoad load = new IRLoad(type, val);
                load.setName("%var" + irFuncEnv.getCounter());
                curBlock.addInstruction(load);
                curValue = load;
                if (type == IRIntType.I8()) {
                    // 需要类型转换到 I32 进行计算
                    curValue = typeTrans(load, IRIntType.I32());
                }
            }
        }
    }

    /**
     * 处理 lVal 是数组的情况
     * symbol -> 数组的符号
     * */
    private void processLValArray2IR(Symbol symbol, ArrayList<IRValue> ind, boolean isLeft) {
        IRValue val = symbol.getIRValue();
        if (val.isParam()) {
            // 此时的 val 存的是数组首地址
            IRGetElePtr gep = null;
            IRLoad load = null;
            if (!val.isAlloc()) {
                IRAlloca alloca = new IRAlloca(val.getType(), "%var" + irFuncEnv.getCounter());
                curBlock.addInstruction(alloca);
                IRStore store = new IRStore(val, alloca);
                curBlock.addInstruction(store);
                // 此时的 load 是指向数组首地址的指针 (i32* / i8*)
                load = new IRLoad(val.getType(), alloca);
                load.setName("%var" + irFuncEnv.getCounter());
                curBlock.addInstruction(load);
                load.setAlloc(true);
                load.setParam(true);
                symbol.setIRValue(load);
                // GEP
                gep = new IRGetElePtr(load, ind);
            } else {
                gep = new IRGetElePtr(val, ind);
            }

            gep.setName("%var" + irFuncEnv.getCounter());
            curBlock.addInstruction(gep);
            if (isLeft) {
                // 是左值，说明要赋值，curValue 应该是一个指针
                curValue = gep;
            } else {
                IRType eleType = ((IRPtrType)gep.getType()).getPointed();
                load = new IRLoad(eleType, gep);
                load.setName("%var" + irFuncEnv.getCounter());
                curBlock.addInstruction(load);
                curValue = load;
                if (eleType == IRIntType.I8())
                    curValue = typeTrans(load, IRIntType.I32());
            }
        } else {
            // 普通数组变量，first 存的是数组首地址的指针
            // val 是指向数组的指针
            ArrayList<IRValue> indexes = new ArrayList<>();
            indexes.add(new IRConstant(IRIntType.I32(), 0));
            indexes.add(ind.get(0));
            IRGetElePtr gep = new IRGetElePtr(val, indexes);
            gep.setName("%var" + irFuncEnv.getCounter());
            curBlock.addInstruction(gep);
            if (isLeft) {
                curValue = gep;
            } else {
                IRLoad load = new IRLoad(((IRPtrType)gep.getType()).getPointed(), gep);
                load.setName("%var" + irFuncEnv.getCounter());
                curBlock.addInstruction(load);
                curValue = load;
                if (load.getType() == IRIntType.I8())
                    curValue = typeTrans(load, IRIntType.I32());
            }
        }
    }

}
