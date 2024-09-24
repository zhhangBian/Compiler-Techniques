package error;

import java.util.ArrayList;

public class ErrorRecorder {
    private static final ArrayList<Error> errorList = new ArrayList<>();

    public static void AddError(Error error) {
        errorList.add(error);
    }

    public static ArrayList<Error> GetErrorList() {
        return errorList;
    }
}
