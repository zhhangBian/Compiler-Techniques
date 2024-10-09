package error;

import java.util.ArrayList;
import java.util.Comparator;

public class ErrorRecorder {
    private static final ArrayList<Error> errorList = new ArrayList<>();

    public static void AddError(Error error) {
        errorList.add(error);
    }

    public static ArrayList<Error> GetErrorList() {
        errorList.sort(Comparator.comparingInt(Error::GetLineNumber));
        return errorList;
    }
}
