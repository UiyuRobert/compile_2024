package Middle;

import ErrorHandling.ErrorHandling;
import Frontend.LexicalAnalysis.Token;
import Frontend.SyntaxAnalysis.Nodes.*;
import Middle.LLVMIR.IRTypes.*;
import Middle.LLVMIR.IRValue;
import Middle.LLVMIR.Values.*;
import Middle.LLVMIR.IRModule;
import Middle.LLVMIR.Values.Instructions.Calculation.IRBinaryInstr;
import Middle.LLVMIR.Values.Instructions.Calculation.IRIcmp;
import Middle.LLVMIR.Values.Instructions.IRCall;
import Middle.LLVMIR.Values.Instructions.IRInstrType;
import Middle.LLVMIR.Values.Instructions.IRInstruction;
import Middle.LLVMIR.Values.Instructions.IRLabel;
import Middle.LLVMIR.Values.Instructions.Terminal.IRBr;
import Middle.LLVMIR.Values.Instructions.TypeCasting.IRTrunc;
import Middle.LLVMIR.Values.Instructions.TypeCasting.IRZext;
import Middle.LLVMIR.Values.Instructions.Memory.IRAlloca;
import Middle.LLVMIR.Values.Instructions.Memory.IRGetElePtr;
import Middle.LLVMIR.Values.Instructions.Memory.IRLoad;
import Middle.LLVMIR.Values.Instructions.Memory.IRStore;
import Middle.LLVMIR.Values.Instructions.Terminal.IRReturn;
import Middle.Symbols.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Visitor {
    public static SymbolTable curTable;
    private final IRModule irModule; // 顶层模块
    private int domainNumber;
    private int inFor;

    private Symbol.Type funcEnv;
    private boolean isFinalStmt;
    private boolean hasReturn;

    private IRFunction irFuncEnv = null; // 所在的函数环境
    private IRBasicBlock curBlock = null; // 当前处理到的基本块
    private IRValue curValue = null; // 计算表达式
    private Stack<Map.Entry<IRLabel, IRLabel>> forStack; // break - continue 要去的标签

    public Visitor() {
        this.domainNumber = 0;
        inFor = 0;
        funcEnv = Symbol.Type.NONE;
        isFinalStmt = false;
        hasReturn = false;
        irModule = new IRModule();
        forStack = new Stack<>();
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
        else if (target == IRIntType.I8() || target == IRIntType.I1())
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

    private IRInstrType getCompareType(String cmp) {
        switch (cmp) {
            case "<": return IRInstrType.Slt;
            case ">": return IRInstrType.Sgt;
            case "<=": return IRInstrType.Sle;
            case ">=": return IRInstrType.Sge;
            default:
                System.out.println("DAMN!! WHAT CMP SYMBOL ??");
                return null;
        }
    }

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
        curBlock = new IRBasicBlock(IRBasicBlock.getBlockName());
        IRLabel label = new IRLabel();
        label.setBelongsTo(curBlock);
        label.setEntry("main");
        curBlock.addInstruction(label);
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
        if (blockItem.isStmt())
            visitStmt(blockItem.getStmt());
        else visitDecl(blockItem.getDecl());
    }

    /*----------------------------------------------- Block End ---------------------------------------------------*/

    /*----------------------------------------------- Stmt Start ---------------------------------------------------*/

    private void visitStmt(StmtNode stmt) {
        StmtNode.Kind kind = stmt.getKind();
        switch (kind) {
            case IFSTMT: {
                IRLabel afterIf = new IRLabel();
                IRBasicBlock block = new IRBasicBlock(IRBasicBlock.getBlockName());
                block.addInstruction(afterIf);
                afterIf.setBelongsTo(block);
                // 进去前建好 if 语句之后的 block
                visitIfStmt(stmt, afterIf);
                // 处理完再更换 block
                curBlock = block;
            } break;
            case BOCSTMT: visitBOCStmt(stmt); break;
            case EXPSTMT: visitExpStmt(stmt); break;
            case FORSTMT: {
                IRLabel afterFor = new IRLabel();
                IRBasicBlock block = new IRBasicBlock(IRBasicBlock.getBlockName());
                block.addInstruction(afterFor);
                afterFor.setBelongsTo(block);

                visitForStmt(stmt, afterFor);
                forStack.pop();
                curBlock = block;
            } break;
            case FUNCSTMT: visitFuncStmt(stmt); break;
            case ASSIGNSTMT: visitAssignStmt(stmt); break;
            case PRINTFSTMT: visitPrintStmt(stmt); break;
            case RETURNSTMT: visitReturnStmt(stmt); break;
            default:
                visitNormalBlock(stmt.getBlock());break;
        }
    }

    private void visitIfStmt(StmtNode ifStmt, IRLabel afterIf) {
        /*-- 'if' '(' Cond ')' Stmt [ 'else' Stmt ] --*/
        isFinalStmt = false;
        boolean hasElse = ifStmt.getElseStmt() != null;

        IRLabel ifTrue = new IRLabel();
        IRBasicBlock ifTrueBlock = new IRBasicBlock(IRBasicBlock.getBlockName());
        ifTrueBlock.addInstruction(ifTrue);
        ifTrue.setBelongsTo(ifTrueBlock);

        if (hasElse) {
            IRLabel elseL = new IRLabel();
            IRBasicBlock elseBlock = new IRBasicBlock(IRBasicBlock.getBlockName());
            elseBlock.addInstruction(elseL);
            elseL.setBelongsTo(elseBlock);

            visitCond(ifStmt.getIfCond(), ifTrue, elseL);
            irFuncEnv.addBlock(curBlock);

            curBlock = ifTrueBlock;
            visitStmt(ifStmt.getIfStmt());
            IRBr br = new IRBr(afterIf);
            curBlock.addInstruction(br);
            irFuncEnv.addBlock(curBlock);

            curBlock = elseBlock;
            visitStmt(ifStmt.getElseStmt());
            br = new IRBr(afterIf);
            curBlock.addInstruction(br);
            irFuncEnv.addBlock(curBlock);
        } else {
            visitCond(ifStmt.getIfCond(), ifTrue, afterIf);
            irFuncEnv.addBlock(curBlock);

            curBlock = ifTrueBlock;
            visitStmt(ifStmt.getIfStmt());
            IRBr br = new IRBr(afterIf);
            curBlock.addInstruction(br);
            irFuncEnv.addBlock(curBlock);
        }

    }

    private void visitForStmt(StmtNode forStmt, IRLabel afterFor) {
        /*-- 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt --*/
        isFinalStmt = false;
        ++inFor;

        IRLabel loopStart = new IRLabel();

        if (forStmt.hasInFor1()) visitInForStmt(forStmt.getInFor1());
        IRBr br = new IRBr(loopStart);
        curBlock.addInstruction(br); // 解决入口问题
        irFuncEnv.addBlock(curBlock);

        IRBasicBlock loopCond = new IRBasicBlock(IRBasicBlock.getBlockName());
        loopCond.addInstruction(loopStart);
        loopStart.setBelongsTo(loopCond);
        curBlock = loopCond;

        IRLabel toBr = new IRLabel(); // 提前生成标签，方便 continue
        forStack.push(new AbstractMap.SimpleEntry<>(afterFor, toBr));

        if (forStmt.hasCondInFor()) {
            IRBasicBlock loopBody = new IRBasicBlock(IRBasicBlock.getBlockName());
            IRLabel body = new IRLabel();
            loopBody.addInstruction(body);
            body.setBelongsTo(loopBody);

            visitCond(forStmt.getCondInFor(), body, afterFor);
            irFuncEnv.addBlock(curBlock);
            curBlock = loopBody;
        }

        visitStmt(forStmt.get4Stmt());
        br = new IRBr(toBr);
        curBlock.addInstruction(br);
        irFuncEnv.addBlock(curBlock);

        curBlock = new IRBasicBlock(IRBasicBlock.getBlockName());
        curBlock.addInstruction(toBr);
        toBr.setBelongsTo(curBlock);

        if (forStmt.hasInFor2()) visitInForStmt(forStmt.getInFor2());

        br = new IRBr(loopStart);
        curBlock.addInstruction(br);
        irFuncEnv.addBlock(curBlock);

        --inFor;
    }

    private void visitInForStmt(ForStmtNode forStmt) {
        /*-- ForStmt → LVal '=' Exp --*/
        Object[] ident = visitLVal(forStmt.getLVal(), true);
        IRValue left = curValue;
        visitExp(forStmt.getExp());
        IRValue right = curValue;
        if (canBeUpdate((String) ident[0], (int) ident[1])) {
            right = typeCheck(right, left);
            IRStore store = new IRStore(right, left);
            store.setName("var" + irFuncEnv.getCounter());
            curBlock.addInstruction(store);
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
            if ((segments[i].equals("%c") || segments[i].equals("%d")) && j < exps.size()) {
                visitExp(exps.get(j++));
                IRValue toPrint = curValue;
                if (toPrint.getType() == IRIntType.I8())
                    toPrint = typeTrans(toPrint, IRIntType.I32());
                IRCall call = null;
                if (segments[i].equals("%c")) {
                    call = new IRCall("putchar", toPrint, null);
                } else
                    call = new IRCall("putint", toPrint, null);

                curBlock.addInstruction(call);
            } else {
                String content = segments[i] + "\\00";
                IRArrayType arrayType = new IRArrayType(IRIntType.I8(), content.length() - 2);
                IRGlobalVariable strConst = new IRGlobalVariable(arrayType, content);
                irModule.addStrPrivate(strConst);
                ArrayList<IRValue> indexes = new ArrayList<>();
                indexes.add(new IRConstant(IRIntType.I64(), 0));
                indexes.add(new IRConstant(IRIntType.I64(), 0));
                IRGetElePtr gep = new IRGetElePtr(strConst, indexes);
                IRCall call = new IRCall("putstr", gep, strConst);
                curBlock.addInstruction(call);
            }
        }
    }

    private void visitBOCStmt(StmtNode bocStmt) {
        /*-- 'break' ';' | 'continue' ';' --*/ // 只能出现在 for 语句块中
        if (inFor == 0 || forStack.isEmpty()) ErrorHandling.processSemanticError("m", bocStmt.getBOCLineNum());
        String op = bocStmt.getBOCStr();
        Map.Entry<IRLabel, IRLabel> entry = forStack.peek();
        IRBr br = null;
        if (op.equals("break")) br = new IRBr(entry.getKey());
        else if (op.equals("continue")) br = new IRBr(entry.getValue());
        else System.out.println("DAMN !!! NO BREAK OR CONTINUE");
        curBlock.addInstruction(br);
    }

    private void visitExpStmt(StmtNode expStmt) {
        /*-- [Exp] ';' --*/
        if (expStmt.hasExp()) visitExp(expStmt.getExp());
    }

    private void visitFuncStmt(StmtNode funcStmt) {
        /*-- LVal '=' 'getint''('')'';' |  LVal '=' 'getchar''('')''; --*/
        curValue = null;
        Object[] ident = visitLVal(funcStmt.getLVal(), true);
        IRValue left = curValue;
        if (canBeUpdate((String) ident[0], (int) ident[1]) && left != null) {
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
        curValue = null;
        Object[] ident = visitLVal(assignStmt.getLVal(), true);
        IRValue left = curValue;
        visitExp(assignStmt.getAssignExp());
        IRValue right = curValue;
        if (canBeUpdate((String) ident[0], (int) ident[1]) && left != null) {
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
            if (isFinalStmt)
                irFuncEnv.addBlock(curBlock);
        } else {
            IRReturn ret = new IRReturn();
            curBlock.addInstruction(ret);
            if (isFinalStmt)
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
                if (symbol != null)
                    symbol.setArrayFirst(first);

            }
            if (symbol != null)
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

                if (symbol != null) {
                    symbol.setIRValue(alloca); // 指向数组的指针
                    symbol.setArrayFirst(first); // 指向数组首元素的指针
                }
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
                if (symbol != null)
                    symbol.setArrayFirst(first);
            }
            if (symbol != null)
                symbol.setIRValue(globalVariable);
        } else {
            if (irType instanceof IRArrayType) {
                IRAlloca alloca = new IRAlloca(irType, "%var" + irFuncEnv.getCounter());
                curBlock.addInstruction(alloca);
                IRValue first = new IRValue(new IRPtrType(((IRArrayType) irType).getElementType()),
                        alloca.getName());
                if (symbol != null) {
                    symbol.setIRValue(alloca);
                    symbol.setArrayFirst(first);
                }
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
                if (symbol != null)
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
            rets = new IRValue[initVal.getExpInits().size()];
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
        curBlock = new IRBasicBlock(IRBasicBlock.getBlockName());
        IRLabel label = new IRLabel();
        label.setEntry(entry.getKey());
        curBlock.addInstruction(label);
        label.setBelongsTo(curBlock);
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
            /* 参数初始化 */
            for(Symbol param : params) {
                IRType irType = param.getIRValue().getType();
                IRAlloca alloca = new IRAlloca(irType, "%var" + irFuncEnv.getCounter());
                curBlock.addInstruction(alloca);
                IRStore store = new IRStore(param.getIRValue(), alloca);
                curBlock.addInstruction(store);
                param.setIRValue(alloca);
                alloca.setParam(true);
            }
        }
        // IR
        irFuncEnv.setType(funcType);
        irModule.addFunction(irFuncEnv);
        if (f != null)
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

    private void visitCond(CondNode cond, IRLabel trueLabel, IRLabel falseLabel) {
        /*-- Cond → LOrExp --*/
        /* cond 为真去 trueLabel, 为假去 falseLabel */
        visitLOrExp(cond.getLOrExp(), trueLabel, falseLabel);
    }

    private void visitLOrExp(LOrExpNode lOrExp, IRLabel trueLabel, IRLabel falseLabel) {
        /*-- LOrExp → LAndExp | LOrExp '||' LAndExp --*/
        List<LAndExpNode> lAndExps = lOrExp.getLAndExps();
        int size = lAndExps.size();
        if (size == 1)
            visitLAndExp(lAndExps.get(0), trueLabel, falseLabel);
        else {
            ArrayList<IRLabel> labels = new ArrayList<>();
            ArrayList<IRBasicBlock> blocks = new ArrayList<>();
            for (int i = 0; i < size - 1; i++) {
                // 分配 size - 1 个标签和基本块
                IRLabel label = new IRLabel();
                labels.add(label);
                IRBasicBlock block = new IRBasicBlock(IRBasicBlock.getBlockName());
                block.addInstruction(label);
                blocks.add(block);

                label.setBelongsTo(block);
            }
            labels.add(falseLabel);
            for (int i = 0; i < size; i++) {
                // 开始依次访问
                if (i != 0) {
                    irFuncEnv.addBlock(curBlock);
                    curBlock = blocks.get(i - 1);
                }
                visitLAndExp(lAndExps.get(i), trueLabel, labels.get(i));
            }
        }
    }

    private void visitLAndExp(LAndExpNode lAndExp, IRLabel trueLabel, IRLabel falseLabel) {
        /*-- LAndExp → EqExp | LAndExp '&&' EqExp --*/
        List<EqExpNode> eqExps = lAndExp.getEqExps();
        int size = eqExps.size();
        if (size == 1)
            visitEqExp(eqExps.get(0), trueLabel, falseLabel);
        else {
            ArrayList<IRLabel> labels = new ArrayList<>();
            ArrayList<IRBasicBlock> blocks = new ArrayList<>();
            for (int i = 0; i < size - 1; i++) {
                IRLabel label = new IRLabel();
                labels.add(label);
                IRBasicBlock block = new IRBasicBlock(IRBasicBlock.getBlockName());
                block.addInstruction(label);
                label.setBelongsTo(block); // 设置对应的基本块
                blocks.add(block);
            }
            labels.add(trueLabel);
            for (int i = 0; i < size; i++) {
                if (i != 0) {
                    irFuncEnv.addBlock(curBlock);
                    curBlock = blocks.get(i - 1);
                }
                visitEqExp(eqExps.get(i), labels.get(i), falseLabel);
            }
        }
    }

    private void visitEqExp(EqExpNode eqExp, IRLabel trueLabel, IRLabel falseLabel) {
        /*-- EqExp → RelExp | EqExp ('==' | '!=') RelExp --*/
        visitRelExp(eqExp.getRelExp());
        IRValue left = curValue;
        IRBr br = null;
        if (eqExp.getRelExps().isEmpty()) {
            if (left.getType() != IRIntType.I1()) {
                IRIcmp icmp = new IRIcmp(left, new IRConstant(IRIntType.I32(), 0), IRInstrType.Ne);
                icmp.setName("%var" + irFuncEnv.getCounter());
                curBlock.addInstruction(icmp);
                left = icmp;
            }
            br = new IRBr(left, trueLabel, falseLabel);
            curBlock.addInstruction(br);
            return;
        }
        /* 所有计算都统一为 i32 */
        if (left.getType() != IRIntType.I32())
            left = typeTrans(left, IRIntType.I32());
        IRInstrType type = null;
        int i = 0;
        for (Map.Entry<RelExpNode, String> entry : eqExp.getRelExps()) {
            ++i;
            type = entry.getValue().equals("==") ? IRInstrType.Eq : IRInstrType.Ne;
            visitRelExp(entry.getKey());
            IRValue right = curValue;
            if (right.getType() != IRIntType.I32())
                right = typeTrans(right, IRIntType.I32());
            IRIcmp cmp = new IRIcmp(left, right, type);
            cmp.setName("%var" + irFuncEnv.getCounter());
            curValue = cmp;
            curBlock.addInstruction(cmp);
            if (i != eqExp.getRelExps().size())
                left = typeTrans(cmp, IRIntType.I32());
        }

        br = new IRBr(curValue, trueLabel, falseLabel);
        curBlock.addInstruction(br);
    }

    private void visitRelExp(RelExpNode relExp) {
        /*-- RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp --*/
        visitAddExp(relExp.getAddExp());
        if (relExp.getAddExps().isEmpty()) return; // 直接 return 回去的是 i32
        IRValue left = curValue;
        IRInstrType type = null;
        int i = 0;

        for (Map.Entry<AddExpNode, String> entry : relExp.getAddExps()) {
            ++i;
            type = getCompareType(entry.getValue());
            visitAddExp(entry.getKey());
            IRValue right = curValue;
            IRIcmp cmp = new IRIcmp(left, right, type);
            cmp.setName("%var" + irFuncEnv.getCounter());
            curValue = cmp;
            curBlock.addInstruction(cmp);
            if (i != relExp.getAddExps().size())
                left = typeTrans(cmp, IRIntType.I32());
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
                IRBinaryInstr subInstr = new IRBinaryInstr(IRInstrType.Sub, IRIntType.I32(), left, right);
                String name = "%var" + irFuncEnv.getCounter();
                subInstr.setName(name);
                curBlock.addInstruction(subInstr);
                curValue = subInstr;
            } else if (entry.getValue().equals("!")) {
                IRIcmp cmp = new IRIcmp(left, right, IRInstrType.Eq); // right == 0 ? 1 : 0
                cmp.setName("%var" + irFuncEnv.getCounter());
                curBlock.addInstruction(cmp);
                curValue = typeTrans(cmp, IRIntType.I32());
            }
            return type;
        }
        else {
            Map.Entry<Token, FuncRParamsNode> entry = unaryExp.getFuncRef();
            Symbol.Type type = visitFuncRef(entry.getKey(), entry.getValue());
            if (curValue.getType() == IRIntType.I8())
                curValue = typeTrans(curValue, IRIntType.I32());
            return type;
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
        if (symbol == null) return;
        IRValue val = symbol.getIRValue();
        if (val.isParam()) { // 对于函数的参数
            // val 就是数组首地址的指针，没有 first
            if (isLeft) {
                curValue = val; // alloc
            } else {
                IRType type = ((IRPtrType)val.getType()).getPointed();
                IRLoad load = new IRLoad(type, val);
                load.setName("%var" + irFuncEnv.getCounter());
                curBlock.addInstruction(load);
                curValue = load;
                if (type == IRIntType.I8())
                    curValue = typeTrans(curValue, IRIntType.I32());
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
            // 此时的 val 存的是指向数组首地址的指针
            IRType type = ((IRPtrType)val.getType()).getPointed();
            IRLoad load = new IRLoad(type, val);
            load.setName("%var" + irFuncEnv.getCounter());
            curBlock.addInstruction(load);
            IRGetElePtr gep = new IRGetElePtr(load, ind);

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
