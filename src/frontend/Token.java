package frontend;
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

    @Override
    public String toString() {
        return kindCode.name() + " " + value;
    }

}
