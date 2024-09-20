package error;

import java.util.ArrayList;

public class ErrorRecorder {
    private static ErrorRecorder errorRecorder;

    private final ArrayList<Integer> errors;

    private ErrorRecorder() {
        this.errors = new ArrayList<>();
    }

    public static ErrorRecorder getInstance() {
        if (errorRecorder == null) {
            errorRecorder = new ErrorRecorder();
        }
        return errorRecorder;
    }
}
