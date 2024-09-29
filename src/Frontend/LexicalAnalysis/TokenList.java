package Frontend.LexicalAnalysis;

import java.util.ArrayList;
import java.util.List;

public class TokenList {
    private List<Token> tokens;
    private int currentIndex;

    public TokenList() {
        tokens = new ArrayList<>();
        currentIndex = 0;
    }

    public void addToken(Token token) {
        tokens.add(token);
    }

    public List<Token> getTokens() {
        return tokens;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Token token : tokens) {
            result.append(token.toString());
        }
        return result.toString();
    }
}
