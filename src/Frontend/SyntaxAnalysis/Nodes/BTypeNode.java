package Frontend.SyntaxAnalysis.Nodes;

import Frontend.LexicalAnalysis.KindCode;
import Frontend.LexicalAnalysis.Token;

public class BTypeNode implements Node {
    /*-- BType → 'int' | 'char'  --*/
    private Token bTypeTerminal;

    public BTypeNode(Token bType) {
        this.bTypeTerminal = bType;
    }

    public String getSymbolType() {
        return bTypeTerminal.getKindCode() == KindCode.INTTK ? "Int" : "Char";
    }

    @Override
    public String toString() {
        return bTypeTerminal.toString();
    }
}
