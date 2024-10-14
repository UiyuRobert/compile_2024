package Frontend.LexicalAnalysis;

import java.util.AbstractMap;
import java.util.Map;

/**
 * 语法单元，包括 单词类别码 和 单词的字符/字符串形式（单词值） 以及 单词所在的行号
 * */
public class Token {
    private KindCode kindCode;
    private String value;
    private int lineNumber;

    public Token(KindCode kindCode, String value,  int lineNumber) {
        this.kindCode = kindCode;
        this.value = value;
        this.lineNumber = lineNumber;
    }

    public KindCode getKindCode() {
        return kindCode;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getValue() { return value; }

    public Map.Entry<String, Integer> getIdentifier() {
        return new AbstractMap.SimpleEntry<>(value, lineNumber);
    }

    @Override
    public String toString() {
        return kindCode.name() + " " + value + '\n';
    }

}
