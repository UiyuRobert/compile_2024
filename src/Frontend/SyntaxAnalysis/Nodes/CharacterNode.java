package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

import java.util.HashMap;

public class CharacterNode implements Node {
    /*-- Character → CharConst  --*/
    private final Token charConstTerminal;
    private static HashMap<String, Integer> ascii = new HashMap<>(){{
        put("\\a", 7);
        put("\\b", 8);
        put("\\t", 9);
        put("\\n", 10);
        put("\\v", 11);
        put("\\f", 12);
        put("\\\"", 34);
        put("\\'", 39);
        put("\\", 92);
        put("\\0", 0);
    }};

    public CharacterNode(Token charConstTerminal) {
        this.charConstTerminal = charConstTerminal;
    }

    public int getValue() {
        String s = charConstTerminal.getValue().substring(1, charConstTerminal.getValue().length() - 1);
        if (ascii.containsKey(s))
            return ascii.get(s);
        return charConstTerminal.getValue().charAt(1);
    }

    @Override
    public String toString() {
        return charConstTerminal.toString() + "<Character>\n";
    }

}
