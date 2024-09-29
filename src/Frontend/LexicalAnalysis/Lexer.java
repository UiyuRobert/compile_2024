package Frontend.LexicalAnalysis;

import ErrorHandling.ErrorHandling;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


/**
 *
 * */
public class Lexer {
    private static final HashMap<String, KindCode> REVERSED = new HashMap<>();
    private final BufferedReader bufferedReader; // 读取器
    private final String text;
    private int currentLineNumber; // 当前处理到的行号
    private int currentCharNumber; // 当前行处理到的字符下标

    static {
        /*--初始化保留字集合--*/
        REVERSED.put("main", KindCode.MAINTK);
        REVERSED.put("const", KindCode.CONSTTK);
        REVERSED.put("int", KindCode.INTTK);
        REVERSED.put("char", KindCode.CHARTK);
        REVERSED.put("break", KindCode.BREAKTK);
        REVERSED.put("continue", KindCode.CONTINUETK);
        REVERSED.put("if", KindCode.IFTK);
        REVERSED.put("else", KindCode.ELSETK);
        REVERSED.put("for", KindCode.FORTK);
        REVERSED.put("getint", KindCode.GETINTTK);
        REVERSED.put("getchar", KindCode.GETCHARTK);
        REVERSED.put("printf", KindCode.PRINTFTK);
        REVERSED.put("return", KindCode.RETURNTK);
        REVERSED.put("void", KindCode.VOIDTK);
    }

    public Lexer(FileReader fileReader) {
        this.bufferedReader = new BufferedReader(fileReader);
        currentLineNumber = 1;
        currentCharNumber = 0;
        text = readText();
    }

    public Token getToken() {
        StringBuilder word = new StringBuilder();
        Token token = null;
        skipWhitespace();
        if (isEndOfFile()) return token;
        if (Character.isLetter(getCurrentChar()) || getCurrentChar() == '_') {
            /*--Ident--*/
            do { word.append(getCharAndMove()); }
            while (!isEndOfFile() && (Character.isLetter(getCurrentChar()) || getCurrentChar() == '_'
                    || Character.isDigit(getCurrentChar())));
            token = processIdent(word.toString());
        } else if (Character.isDigit(getCurrentChar())) {
            /*--IntConst--*/
            do { word.append(getCharAndMove()); }
            while (!isEndOfFile() && Character.isDigit(getCurrentChar()));
            token = new Token(KindCode.INTCON, word.toString(), currentLineNumber);
        } else if (getCurrentChar() == '\'') {
            /*--CharConst--*/
            word.append(getCharAndMove());
            if (getCurrentChar() >= 32 && getCurrentChar() < 127) {
                word.append(getCharAndMove()).append(getCharAndMove()); // append 2
                if (getCurrentChar() == '\'') word.append(getCharAndMove());
            }
            token = new Token(KindCode.CHRCON, word.toString(), currentLineNumber);
        } else if (getCurrentChar() == '"') {
            /*--StringConst--*/
            word = readStringConst();
            token = new Token(KindCode.STRCON, word.toString(), currentLineNumber);
        } else if (getCurrentChar() == '/') {
            /*--DIV, comment--*/
            if (text.charAt(currentCharNumber + 1) == '/') {
                /*--处理行注释--*/
                processLineComment();
            } else if (text.charAt(currentCharNumber + 1) == '*') {
                /*--处理块注释--*/
                processBlockComment();
            } else {
                /*--DIV--*/
                token = new Token(KindCode.DIV, String.valueOf(getCharAndMove()), currentLineNumber);
            }
        } else {
            char currentChar = getCharAndMove();
            switch (currentChar) {
                case '(': token = new Token(KindCode.LPARENT, "(", currentLineNumber); break;
                case ')': token = new Token(KindCode.RPARENT, ")", currentLineNumber); break;
                case '[': token = new Token(KindCode.LBRACK, "[", currentLineNumber); break;
                case ']': token = new Token(KindCode.RBRACK, "]", currentLineNumber); break;
                case '{': token = new Token(KindCode.LBRACE, "{", currentLineNumber); break;
                case '}': token = new Token(KindCode.RBRACE, "}", currentLineNumber); break;
                case '+': token = new Token(KindCode.PLUS, "+", currentLineNumber); break;
                case '-': token = new Token(KindCode.MINU, "-", currentLineNumber); break;
                case '*': token = new Token(KindCode.MULT, "*", currentLineNumber); break;
                case '%': token = new Token(KindCode.MOD, "%", currentLineNumber); break;
                case ';': token = new Token(KindCode.SEMICN, ";", currentLineNumber); break;
                case ',': token = new Token(KindCode.COMMA, ",", currentLineNumber); break;
                case '!': token = processNotNeq(); break;
                case '=': token = processEqlAssign(); break;
                case '>': token = processGreGeq(); break;
                case '<': token = processLssLeq(); break;
                case '&': token = processAndErr(); break;
                case '|': token = processOrErr(); break;
                default: break;
            }
        }
        return token;
    }

    private Token processIdent(String ident) {
        Token token = null;
        if (REVERSED.containsKey(ident)) {
            token = new Token(REVERSED.get(ident), ident, currentLineNumber);
        } else {
            token = new Token(KindCode.IDENFR, ident, currentLineNumber);
        }
        return token;
    }

    private Token processOrErr() {
        Token token = null;
        if (getCurrentChar() == '|') {
            token = new Token(KindCode.OR, "||", currentLineNumber);
            move(1);
        } else {
            token = ErrorHandling.processLexicalError('|', currentLineNumber);
        }
        return token;
    }

    private Token processAndErr() {
        Token token = null;
        if (getCurrentChar() == '&') {
            token = new Token(KindCode.AND, "&&", currentLineNumber);
            move(1);
        } else {
            token = ErrorHandling.processLexicalError('&', currentLineNumber);
        }
        return token;
    }

    private Token processLssLeq() {
        Token token = null;
        if (getCurrentChar() == '=') {
            token = new Token(KindCode.LEQ, "<=", currentLineNumber);
            move(1);
        } else {
            token = new Token(KindCode.LSS, "<", currentLineNumber);
        }
        return token;
    }

    private Token processGreGeq() {
        Token token = null;
        if (getCurrentChar() == '=') {
            token = new Token(KindCode.GEQ, ">=", currentLineNumber);
            move(1);
        } else {
            token = new Token(KindCode.GRE, ">", currentLineNumber);
        }
        return token;
    }

    private Token processEqlAssign() {
        Token token = null;
        if (getCurrentChar() == '=') {
            token = new Token(KindCode.EQL, "==", currentLineNumber);
            move(1);
        } else {
            token = new Token(KindCode.ASSIGN, "=", currentLineNumber);
        }
        return token;
    }

    private Token processNotNeq() {
        Token token = null;
        if (getCurrentChar() == '=') {
            token = new Token(KindCode.NEQ, "!=", currentLineNumber);
            move(1);
        } else {
            token = new Token(KindCode.NOT, "!", currentLineNumber);
        }
        return token;
    }

    private void processBlockComment() {
        move(2); // 跳过当前 /*
        while (!(getCurrentChar() == '*' && text.charAt(currentCharNumber + 1) == '/')) {
            if (getCurrentChar() == '\n') ++currentLineNumber;
            move(1);
        }
        move(2); // 跳过结尾 */
    }

    private void processLineComment() {
        while (getCharAndMove() != '\n');
        currentLineNumber++;
    }

    private StringBuilder readStringConst() {
        /*--StringConst--*/
        boolean escaped = false; // 是否是转义状态
        StringBuilder stringConst = new StringBuilder(String.valueOf(getCharAndMove())); // 拼接最初的 "
        while (!isEndOfFile()) {
            char currentChar = getCurrentChar();
            stringConst.append(currentChar);
            if (escaped) escaped = false;
            else if (currentChar == '\\') escaped = true;
            else if (currentChar == '"') break;
            move(1);
        }
        move(1); // 跳过最后的 "
        return stringConst;
    }

    private String readText() {
        String line;
        StringBuilder textContent = new StringBuilder();
        while (true) {
            try {
                if ((line = bufferedReader.readLine()) == null) break;
                textContent.append(line).append("\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return textContent.toString();
    }

    private void skipWhitespace() {
        while(!isEndOfFile() && Character.isWhitespace(getCurrentChar())) {
            if (getCurrentChar() == '\n') currentLineNumber++;
            move(1);
        }
    }

    private char getCurrentChar() {
        return text.charAt(currentCharNumber);
    }

    private void move(int step) {
        currentCharNumber += step;
    }

    private char getCharAndMove() {
        char currentChar = getCurrentChar();
        move(1);
        return currentChar;
    }

    public boolean isEndOfFile() {
        return currentCharNumber >= text.length();
    }

}

