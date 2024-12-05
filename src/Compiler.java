import BackEnd.MipsBuilder;
import Frontend.LexicalAnalysis.Lexer;
import Frontend.LexicalAnalysis.Token;
import Frontend.LexicalAnalysis.TokenList;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ErrorHandling.ErrorHandling;
import Frontend.SyntaxAnalysis.Nodes.CompUnitNode;
import Frontend.SyntaxAnalysis.Parser;
import Middle.LLVMIR.IRModule;
import Middle.Visitor;

import static Middle.Symbols.SymbolTable.record;

public class Compiler {
    private static final String SYNTAX = "parser.txt";
    private static String parserResult = null;
    private static final String SYMBOL = "symbol.txt";
    private static String symbolResult = null;
    private static final String LLVM = "llvm_ir.txt";
    private static String llvmResult = null;
    private static final String MIPS = "mips.txt";
    private static String mipsResult = null;

    private static int option = 3;

    public static void main(String[] args) throws FileNotFoundException {
        /*--参数设置--*/
        String inputFileName = "testfile.txt";
        /*--初始化 Lexer--*/
        FileReader fileReader = new FileReader(inputFileName);
        Lexer lexer = new Lexer(fileReader);
        /*--设置 TokenList--*/
        TokenList tokenList = createTokenList(lexer);
        // System.out.println(tokenList.toString());
        /*-- 启动 Parser --*/
        Parser parser = new Parser(tokenList);
        CompUnitNode compUnit = parser.parse();
        parserResult = parser.getParseResult();
        // System.out.println(parser.getParseResult());
        /*-- 语义分析，中间代码生成 --*/
        Visitor visitor = new Visitor();
        IRModule module = visitor.visit(compUnit);
        symbolResult = sortAndGen(record); // 符号表
        llvmResult = module.getIR();
        module.toAssembly();
        mipsResult = MipsBuilder.builder().getResult();
        //System.out.println(mipsResult);
        /*--判断是否有错误--*/
        if (ErrorHandling.isErrorOccurred()) {
            errorOutput();
        } else {
            normalOutput();
            // System.out.println(parserResult);
            // System.out.println(result);
        }
    }

    private static TokenList createTokenList(Lexer lexer) {
        TokenList tokens = new TokenList();
        while (!lexer.isEndOfFile()) {
            Token token = lexer.getToken();
            if (token != null) {
                tokens.addToken(token);
            }
        }
        return tokens;
    }

    private static void errorOutput() {
        String outputErrorFileName = "error.txt";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputErrorFileName));
            writer.write(ErrorHandling.getErrors());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Error !");
    }

    private static void normalOutput() {
        String outputRightFileName = null;
        String output = null;
        switch (option) {
            case 0:
                outputRightFileName = SYNTAX;
                output = parserResult;
                break;
            case 1:
                outputRightFileName = SYMBOL;
                output = symbolResult;
                break;
            case 2:
                outputRightFileName = LLVM;
                output = llvmResult;
                break;
            case 3:
                outputRightFileName = MIPS;
                output = mipsResult;
                break;

            default: outputRightFileName = "output.txt"; break;
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputRightFileName));
            writer.write(output);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Success !");
    }

    private static String sortAndGen(List<String> arr) {
        Collections.sort(arr, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String[] arr1 = o1.split(" ");
                String[] arr2 = o2.split(" ");
                return Integer.parseInt(arr1[0]) - Integer.parseInt(arr2[0]);
            }
        });
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            sb.append(s).append('\n');
        }
        return sb.toString();
    }
}