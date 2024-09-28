package Frontend.LexicalAnalysis;

import java.util.regex.Pattern;

/**
 * 采用 类别码（正则表达式）的形式表达
 * */
public enum KindCode {
    IDENFR("^[_A-Za-z]\\w*$"), // 标识符
    INTCON("^\\d+$"), // 整数常量
    STRCON("^\"(\\\\.|[^\\\\\"])*\"$"), // 字符串常量
    CHRCON("^'(\\\\.|[^\\\\'])'$"), // 字符常量

    /*--关键字--*/
    MAINTK("^main$"),
    CONSTTK("^const$"),
    INTTK("^int$"),
    CHARTK("^char$"),
    BREAKTK("^break$"),
    CONTINUETK("^continue$"),
    IFTK("^if$"),
    ELSETK("^else$"),
    FORTK("^for$"),
    GETINTTK("^getint$"),
    GETCHARTK("^getchar$"),
    PRINTFTK("^printf$"),
    RETURNTK("^return$"),
    VOIDTK("^void$"),

    /*--逻辑运算符--*/
    NOT("^!$"),
    AND("^&&$"),
    OR("^\\|\\|$"),

    /*--运算--*/
    PLUS("^\\+$"),
    MINU("^\\-$"),
    MULT("^\\*$"),
    DIV("^/$"),
    MOD("^%$"),

    /*--比较--*/
    LSS("^<$"),
    LEQ("^<=$"),
    GRE("^>$"),
    GEQ("^>=$"),
    EQL("^==$"),
    NEQ("^!=$"),

    /*--括号--*/
    LPARENT("^\\($"),
    RPARENT("^\\)$"),
    LBRACK("^\\[$"),
    RBRACK("^\\]$"),
    LBRACE("\\{"),
    RBRACE("^\\}$"),

    /*--其他--*/
    ASSIGN("^=$"),
    SEMICN("^;$"),
    COMMA("^,$"),
    ;

    private Pattern pattern;

    KindCode(String patternString) {
        this.pattern = Pattern.compile(patternString);
    }

    public Pattern getPattern() {
        return this.pattern;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
