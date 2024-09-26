package ErrorHandling;

import java.util.ArrayList;

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

    public ErrorHandling() {
    }

    public static void addError(String errorCode, int errorLineNumber) {
        errors.add(new MError(errorCode, errorLineNumber));
        if (!errorOccurred) {
            errorOccurred = true;
        }
    }

    public static boolean isErrorOccurred() {
        return errorOccurred;
    }

    public static String getErrors() {
        StringBuilder sb = new StringBuilder();
        for (MError error : errors) {
            sb.append(error.toString());
        }
        return sb.toString();
    }
}
