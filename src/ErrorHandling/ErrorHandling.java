package ErrorHandling;

import Frontend.LexicalAnalysis.KindCode;
import Frontend.LexicalAnalysis.Token;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ErrorHandling {
    static class MError {
        private final String errorCode;
        private final int errorLineNumber;

        public MError(String errorCode, int errorLineNumber) {
            this.errorCode = errorCode;
            this.errorLineNumber = errorLineNumber;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public int getErrorLineNumber() {
            return errorLineNumber;
        }

        @Override
        public String toString() {
            return errorLineNumber + " " + errorCode + "\n";
        }

    }
    private static boolean errorOccurred = false;
    private static ArrayList<MError> errors = new ArrayList<MError>();

    public ErrorHandling() {}

    public static void addError(String errorCode, int errorLineNumber) {
        errors.add(new MError(errorCode, errorLineNumber));
        if (!errorOccurred) {
            errorOccurred = true;
        }
    }

    public static Token processLexicalError(char errCh, int errorLineNumber) {
        if (errCh == '|') {
            addError("a", errorLineNumber);
            return new Token(KindCode.OR, "|", errorLineNumber);
        } else {
            addError("a", errorLineNumber);
            return new Token(KindCode.AND, "&", errorLineNumber);
        }
    }

    public static Token processSyntaxError(KindCode expected, int errorLineNumber) {
        if (expected == KindCode.SEMICN) {
            addError("i", errorLineNumber);
            return new Token(expected, ";", errorLineNumber);
        } else if (expected == KindCode.RPARENT) {
            addError("j", errorLineNumber);
            return new Token(KindCode.RPARENT, ")", errorLineNumber);
        } else {
            addError("k", errorLineNumber);
            return new Token(expected, "]", errorLineNumber);
        }
    }

    public static void processSemanticError(String errCode, int errorLineNumber) {
        addError(errCode, errorLineNumber);
    }

    public static boolean isErrorOccurred() {
        return errorOccurred;
    }

    public static String getErrors() {
        Collections.sort(errors, new Comparator<MError>() {
            @Override
            public int compare(MError o1, MError o2) {
                return o1.errorLineNumber - o2.errorLineNumber;
            }
        });
        StringBuilder sb = new StringBuilder();
        for (MError error : errors) {
            sb.append(error.toString());
        }
        return sb.toString();
    }
}
