package error;

import java.util.ArrayList;
import java.util.Comparator;

public class ErrorRecorder {
    private static final ArrayList<Error> errorList = new ArrayList<>();
    private static boolean recordError = true;

    public static void AddError(Error error) {
        if (recordError) {
            errorList.add(error);
        }
    }

    public static void SetStopRecordError() {
        recordError = false;
    }

    public static void SetStartRecordError() {
        recordError = true;
    }

    public static ArrayList<Error> GetErrorList() {
        errorList.sort(Comparator.comparingInt(Error::GetLineNumber));
        return errorList;
    }
}
