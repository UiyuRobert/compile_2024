import Frontend.LexicalAnalysis.Lexer;
import Frontend.LexicalAnalysis.Token;
import Frontend.LexicalAnalysis.TokenList;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
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
    public static void main(String[] args) throws FileNotFoundException {
        /*--参数设置--*/
        String inputFileName = "testfile.txt";
        String result;
        /*--初始化 Lexer--*/
        FileReader fileReader = new FileReader(inputFileName);
        Lexer lexer = new Lexer(fileReader);
        /*--设置 TokenList--*/
        TokenList tokenList = createTokenList(lexer);
        // System.out.println(tokenList.toString());
        /*-- 启动 Parser --*/
        Parser parser = new Parser(tokenList);
        CompUnitNode compUnit = parser.parse();
        result = parser.getParseResult();
        // System.out.println(parser.getParseResult());
        /*-- 语义分析 --*/
        Visitor visitor = new Visitor();
        IRModule module = visitor.visit(compUnit);
        result = sortAndGen(record);
        /*--判断是否有错误--*/
        if (ErrorHandling.isErrorOccurred()) {
            errorOutput();
        } else {
            normalOutput(result);
            System.out.println(module.getIR());
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
    }

    private static void normalOutput(String output) {
        String outputRightFileName = "parser.txt";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputRightFileName));
            writer.write(output);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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