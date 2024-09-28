package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.Token;

public class CharacterNode implements Node {
    /*-- Character → CharConst  --*/
    private Token charConstTerminal;

    public CharacterNode(Token charConstTerminal) {
        this.charConstTerminal = charConstTerminal;
    }
}
