package utils;

public class Debug {
    public static void DebugPrint(Object object) {
        if (Setting.DEBUG) {
            System.out.println(object);
        }
    }

    public static void DebugThrow(String message) {
        throw new RuntimeException(message);
    }
}
