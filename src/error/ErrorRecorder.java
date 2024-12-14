package error;

import java.util.ArrayList;
import java.util.TreeMap;

public class ErrorRecorder {
    private static final TreeMap<Integer, Error> errorList = new TreeMap<>();
    private static boolean recordError = true;

    public static boolean HaveNoError() {
        return errorList.isEmpty();
    }

    public static void AddError(Error error) {
        if (recordError) {
            if (!errorList.containsKey(error.GetLineNumber())) {
                errorList.put(error.GetLineNumber(), error);
            }
        }
    }

    public static void SetStopRecordError() {
        recordError = false;
    }

    public static void SetStartRecordError() {
        recordError = true;
    }

    public static ArrayList<Error> GetErrorList() {
        return new ArrayList<>(errorList.values());
    }
}
