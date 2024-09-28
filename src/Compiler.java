import Frontend.LexicalAnalysis.Lexer;
import Frontend.LexicalAnalysis.Token;
import Frontend.LexicalAnalysis.TokenList;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import ErrorHandling.ErrorHandling;

public class Compiler {
    public static void main(String[] args) throws FileNotFoundException {
        /*--参数设置--*/
        String inputFileName = "testfile.txt";
        /*--初始化 Lexer--*/
        FileReader fileReader = new FileReader(inputFileName);
        Lexer lexer = new Lexer(fileReader);
        /*--设置 TokenList--*/
        TokenList tokenList = createTokenList(lexer);
        /*--判断是否有错误--*/
        if (ErrorHandling.isErrorOccurred()) {
            errorOutput();
        } else {
            normalOutput(tokenList);
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

    private static void normalOutput(TokenList tokenList) {
        String outputRightFileName = "lexer.txt";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputRightFileName));
            writer.write(tokenList.toString());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}