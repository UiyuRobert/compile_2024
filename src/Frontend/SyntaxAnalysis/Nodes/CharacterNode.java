package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

public class CharacterNode implements Node {
    /*-- Character → CharConst  --*/
    private final Token charConstTerminal;

    public CharacterNode(Token charConstTerminal) {
        this.charConstTerminal = charConstTerminal;
    }

    public int getValue() {
        return charConstTerminal.getValue().charAt(1);
    }

    @Override
    public String toString() {
        return charConstTerminal.toString() + "<Character>\n";
    }

}
